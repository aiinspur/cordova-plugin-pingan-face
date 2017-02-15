//
//  PAFaceCheckHome.m
//  PAFaceCheck
//
//  Created by prliu on 16/2/18.
//  Copyright © 2016年 PingAN. All rights reserved.
//

#import "PAFaceCheckHome.h"
#import "PAZCLDefineTool.h"
#import "PALivenessDetector.h"
#import "PABottomView.h"
#import "PAPromptView.h"
#import "SendFailView.h"
#import "PAFaceInfo.h"
#import "PAFaceSetting.h"
#import "NSObject+PASBJSON.h"
#import "PACircularRing.h"
#import <sys/utsname.h>
#include <CoreGraphics/CGAffineTransform.h>
#import <CoreImage/CoreImage.h>
#import <ImageIO/ImageIO.h>
#import <AssertMacros.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import "PAZCLDefineTool.h"
#import "PAZCLTools.h"

#define increaseRect 60
#define increaseRectOrNo 1  //是否扩充剪脸部位 1为YES(快乐平安) 0为NO
#define afterTime 10
#define detectFaces 1



@interface PAFaceCheckHome () <PALivenessProtocolDelegate, AVCaptureVideoDataOutputSampleBufferDelegate, AVAudioPlayerDelegate, PAPromptDelegate, SendViewDelegate,AVSpeechSynthesizerDelegate>
{
    NSInteger _curStep;           //当前完成步骤
    AVAudioPlayer *facePlayer;    //语音
    UIImageView *barImage;
    UILabel *barStep;
    
    PAPromptView *promptView;
    
    NSTimer *playerTimer;
    NSString *soundStr;
    
    UIImage *personImage;
    NSString *imageId;
    PALivenessDetectionFrame *personImageDetection;
    
    NSString *type;
    
    PACircularRing *noFaceConterRing;
    NSInteger _maxTime;
    NSInteger _count;
    
    
    SendFailView *sendView;
    
    
    CGFloat thresholdValue;
    CGFloat fuzzyValue;
    
    UIImage *okImage;
    
    int timedelay;
    
    //底部
    UIView *_FaceCheckBV;
    UIImageView *bg;//采集框

    BOOL stateOk;
    NSInteger single;
    
    
    
}

@property (nonatomic, weak) id <PACheckDelegate>delegate;

@property (nonatomic, strong) AVCaptureSession *session;
@property (nonatomic, strong) AVCaptureStillImageOutput *output;
@property (nonatomic, strong) AVCaptureDeviceInput *videoInput;
@property(nonatomic, readwrite) AVCaptureVideoOrientation videoOrientation;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *previewLayer;
@property (nonatomic, strong) AVCaptureDevice *device;

@property (nonatomic, strong) NSString *cardId;

@property (nonatomic, strong) PALivenessDetector *livenessDetector;
@property (nonatomic, assign) PALivenessDetectionType types;

@property (nonatomic, assign) BOOL starLiveness;            //表示是否开启活体检测
@property (nonatomic, strong) PABottomView *bottomView;
@property (nonatomic, strong) NSMutableArray *actionArray;  //活体动作数据
@property (nonatomic, strong) NSString *interfaceType;
@property (nonatomic) BOOL soundContor;
@property (nonatomic) BOOL faceControl;
@property (nonatomic) BOOL simplificationContor;
@property (nonatomic) BOOL countDown;
@property (nonatomic) BOOL isOk;
@property (nonatomic) BOOL isEnvironmentOk;
@property (nonatomic) BOOL firstOk;
@property (nonatomic) BOOL glassesSwitch;
@property (nonatomic) BOOL advertisingSwitch;

@property (retain, nonatomic) IBOutlet UIView *cameraView;
@property (retain, nonatomic) IBOutlet UIImageView *smalImage;
@property (retain, nonatomic) IBOutlet UIImageView *imageView;


@property (nonatomic, strong) UILabel *numLabel;
@property (nonatomic, strong) UILabel *advertising;
@property (nonatomic, strong) NSTimer *timerCoundDown;
@property (nonatomic, strong) UIImageView *rotationView;

@property (nonatomic, strong)NSString *advertisingStr;
@property (nonatomic, assign)NSString *number0fAction;


@property (nonatomic, strong) AVSpeechSynthesizer *synthesizer;     //Text转语音
@property (nonatomic, strong) AVSpeechUtterance *utterance;
@property (nonatomic, strong) AVSpeechSynthesisVoice *voiceType;

@property(nonatomic) CGFloat brightness NS_AVAILABLE_IOS(5_0);        // 0 .. 1.0, where 1.0 is maximum brightness. Only supported by main screen.

@property (nonatomic, retain) CALayer *customLayer;


@property(nonatomic) int currentStep;

@end

@implementation PAFaceCheckHome

- (id)initWithPAcheckWithTheCountdown :(BOOL)countDown andTheAdvertising:(NSString*)advertising  number0fAction:(NSString*)num voiceSwitch:(BOOL)voiceSwitch delegate:(id <PACheckDelegate>)faceDelegate
{
    self = [super init];
    if (self) {
        
        self.delegate = faceDelegate;
        if ( advertising && ![advertising isEqualToString:@""] && ![advertising isEqualToString:@"(nil)"]) {
            self.advertisingSwitch = YES;
            self.advertisingStr = advertising;
            
        }else{
            self.advertisingSwitch = NO;
        }
        self.number0fAction = num;
        self.countDown = countDown;
        self.soundContor = voiceSwitch;
    }
    return self;
}

- (void)viewDidLoad{
    [super viewDidLoad];
    
    single = 0;
    self.isEnvironmentOk = NO;
    
    self.view.backgroundColor = [UIColor blackColor];
    [self initBar];             //初始化bar
    [self initFaceActionView];  //动作提示界面
    [self initAVSpeech];        //初始化语音
    [self initialSession];      //初始化相机
    [self createFacecheck];     //FaceCheck初始化
    [self setUpCameraLayer];    //加载图层预览
    [self startSession];        //开启相机
    [self createTheBackButton]; //返回按钮
    
    //等待动画提示结束才能调用YES
    [self initHardCode:NO];
    
    
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    
}

-(void)viewDidAppear:(BOOL)animated{
    
    [super viewDidAppear:animated];
    [self promptViewFinish];
    
}

-(void)createTheBackButton{
    
    UIImage *butImage = kFaceImage(@"Face_backBut");
    UIButton *backButton = [[UIButton alloc]init];
    [backButton setBackgroundColor:[UIColor clearColor]];
    backButton.translatesAutoresizingMaskIntoConstraints = NO;
    [backButton setBackgroundImage:butImage forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(backToAppAnimatedWhenClickBlack:) forControlEvents:UIControlEventTouchDown];
    [_FaceCheckBV addSubview:backButton];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:backButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeRight multiplier:0.1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:backButton attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:backButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.35 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:backButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.35 constant:0]];
    
    
}
/**
 *  返回
 */
- (void)backToAppAnimatedWhenClickBlack:(UIButton*)blackB{
    
    [self checkFail:DETECTION_FAILED_TYPE_Interrupt];
    
}
#pragma  mark -- 不做活体检测时的倒计时
-(void)createNoFaceContorTimeView:(int)Maxtime{
    
    _count = 0;
    _maxTime = Maxtime;
    
    self.rotationView = [[UIImageView alloc] initWithImage:kFaceImage(@"Face_timeAnimation")];
    
    self.numLabel = [[UILabel alloc] init];
    [self.numLabel setTextAlignment:NSTextAlignmentCenter];
    [self.numLabel setFont:[UIFont systemFontOfSize:kScaleWidth(18)]];
    [self.numLabel setTextColor:kTextColor];
    [self.numLabel setBackgroundColor:[UIColor clearColor]];
    self.numLabel.text = [NSString stringWithFormat:@"%ld", (long)_maxTime];
    
    //添加约束
    self.rotationView.translatesAutoresizingMaskIntoConstraints = NO;;
    self.numLabel.translatesAutoresizingMaskIntoConstraints = NO;;
    
    [_FaceCheckBV addSubview:self.numLabel];
    [_FaceCheckBV addSubview:self.rotationView];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.rotationView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeRight multiplier:0.9 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.rotationView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.rotationView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.30 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.rotationView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.30 constant:0]];
    
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.rotationView attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.rotationView attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.30 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:0.30 constant:0]];
    
    //是否隐藏
    [self.rotationView setHidden:YES];
    [self.numLabel setHidden:YES];
    
}


- (void)startAnimation{
    
    if (self.countDown) {
        [self.rotationView setHidden:NO];
        [self.numLabel setHidden:NO];
    }else{
        [self.rotationView setHidden:YES];
        [self.numLabel setHidden:YES];
    }
    [self.view addSubview:self.rotationView];
    [self.view addSubview:self.numLabel];
    
    CABasicAnimation* rotationAnimation;
    rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0];
    rotationAnimation.duration = 1.0f;
    rotationAnimation.cumulative = YES;
    rotationAnimation.repeatCount = kActionTime;
    [self.rotationView.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
    
    
}
- (void)stopAnimations{
    
    [self.rotationView.layer removeAllAnimations];
    [self.timerCoundDown invalidate];
    self.timerCoundDown = nil;
    [self.rotationView setHidden:YES];
    [self.numLabel setHidden:YES];
    
}

#pragma  mark -- 人脸识别请求类,封装Head/Body （抠图）
- (void)sendImage:(PALivenessDetectionFrame*)imageFrame{
    

    MAIN_ACTION((^(){
        
        //封装图片信息
        PAFaceAttr attr = imageFrame.attr;
        NSString *text = [NSString stringWithFormat:@"\n是否包含人脸:\n%@\n左右旋转角度:\n%f\n上下旋转角度:\n%f\n运动模糊程度:\n%f\n人脸位置:%f %f\n%f %f\n亮度:\n%f\n",
                          attr.has_face?@"有人脸":@"无人脸",
                          attr.yaw,
                          attr.pitch,
                          attr.blurness_motion,
                          attr.face_rect.origin.x, attr.face_rect.origin.y,
                          attr.face_rect.size.width, attr.face_rect.size.height,
                          attr.brightness
                          ];
        
        
        //NSLog(@"info:%@",text);
        
        NSMutableDictionary *imageInfo = [[NSMutableDictionary alloc]init];
        NSString *hasFactOrNo;
        attr.has_face ? hasFactOrNo = @"ture" : hasFactOrNo = @"false";
        
        [imageInfo setValue:hasFactOrNo forKey:@"has_face"];//是否有人脸
        
        
        [imageInfo setValue:[NSString stringWithFormat:@"%f",attr.yaw] forKey:@"deflection_h"];//水平偏转角度
        [imageInfo setValue:[NSString stringWithFormat:@"%f",attr.pitch] forKey:@"deflection_v"];//上下偏转角度
        [imageInfo setValue:[NSString stringWithFormat:@"%f",attr.blurness_motion] forKey:@"blurness_motion"];//运动模糊程度
        
        //[imageInfo setValue:[NSString stringWithFormat:@"%d",attr.eye_hwratio] forKey:@"eye_left_hwratio"];//眼睛睁闭程度
        [imageInfo setValue:[NSString stringWithFormat:@"%f",attr.brightness] forKey:@"brightness"];//亮度
        
        [imageInfo setValue:[NSString stringWithFormat:@"%@",@"jpg"] forKey:@"content_type"];////图片类型
        [imageInfo setValue:[NSString stringWithFormat:@"%@",text] forKey:@"infoText"];
        
        
        //根据区域进行图片截取
        UIImage *result_image = [imageFrame croppedImageOfFace];

        NSMutableDictionary *rectDict = [[NSMutableDictionary alloc]init];
        [rectDict setValue:[NSString stringWithFormat:@"%f",attr.face_rect.origin.x] forKey:@"face_rect_top_left"];//人脸在轮廓区域的位置X
        [rectDict setValue:[NSString stringWithFormat:@"%f",attr.face_rect.origin.y] forKey:@"face_rect_top_right"];//人脸在轮廓区域的位置Y
        [rectDict setValue:[NSString stringWithFormat:@"%f",attr.face_rect.size.width] forKey:@"face_rect_width"];//人脸在轮廓区域的位置width
        [rectDict setValue:[NSString stringWithFormat:@"%f",attr.face_rect.size.height] forKey:@"face_rect_height"];//人脸在轮廓区域的位置height
        [imageInfo setValue:rectDict forKey:@"face_rect"];//人脸位置
        [imageInfo setValue:[NSString stringWithFormat:@"%f",result_image.size.width] forKey:@"width"];//图片宽度
        [imageInfo setValue:[NSString stringWithFormat:@"%f",result_image.size.height] forKey:@"height"];//图片高度
        
        
        //结束
        [self backToAppAnimated:NO];
        
        if (self.navigationController && self.navigationController.viewControllers.count > 1) {
                    
                    [self.navigationController popViewControllerAnimated:NO];
                    
         } else {
                    
            [self dismissViewControllerAnimated:NO completion:^{
                
                if (self.delegate && [self.delegate respondsToSelector:@selector(getPacheckreportWithImage:andTheFaceImage:andTheFaceImageInfo:andTheResultCallBlacek:)]){
                    
                    [self.delegate getPacheckreportWithImage:imageFrame.image andTheFaceImage:result_image andTheFaceImageInfo:imageInfo  andTheResultCallBlacek:^(NSDictionary *content) {
                        
                        if (content) {
                            
                            if (sendView) {
                                [sendView stopLoading];
                            }
                            
                        }
                    }];
                }

                
            }];
         }
    }));
    
}


/**
 *  初始化bar
 */
- (void)initBar{
    
    
    //采集框
    bg = [[UIImageView alloc] init];
    bg.translatesAutoresizingMaskIntoConstraints = NO;
    
    if (kScreenWidth == 320) {
        [bg setImage:kFaceImage(@"PAFace6401136@2x.png")];
    }else if(kScreenWidth == 375){
        [bg setImage:kFaceImage(@"PAFace7501334@2x.png")];
    }else if(kScreenWidth == 414){
        [bg setImage:kFaceImage(@"PAFace12422208@2x.png")];
    }else{
        [bg setImage:kFaceImage(@"PAFace12422208@2x.png")];
    }
    
    [self.view addSubview:bg];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:bg attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1 constant:-10]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:bg attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1 constant:10]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:bg attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:bg attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    
    
    
    //底层蒙板
    _FaceCheckBV = [[UIView alloc]init];
    _FaceCheckBV.translatesAutoresizingMaskIntoConstraints = NO;
    _FaceCheckBV.backgroundColor = [UIColor clearColor];
    [self.view addSubview:_FaceCheckBV];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:_FaceCheckBV attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1 constant:-10]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:_FaceCheckBV attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1 constant:10]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:_FaceCheckBV attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:_FaceCheckBV attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:0.2 constant:0]];
    
    //广告语
    if (self.advertisingSwitch) {
        
        UIFont *fontName = [UIFont systemFontOfSize:16.0f];
        
        
        self.advertising = [[UILabel alloc]init];
        self.advertising.alpha = 0.6;
        [self.advertising setFont:fontName];
        [self.advertising setTextColor:[UIColor blackColor]];
        [self.advertising setText:self.advertisingStr];
        self.advertising.textAlignment  = NSTextAlignmentCenter;
        
        self.advertising.numberOfLines = 0;
        self.advertising.lineBreakMode = NSLineBreakByWordWrapping;
        
        CGSize sizeName = [PAZCLTools getTextSizeWithString:self.advertisingStr font:fontName size:CGSizeMake(kScreenWidth,MAXFLOAT)];
        
        
        [self.view addSubview:self.advertising];
        
        self.advertising.translatesAutoresizingMaskIntoConstraints = NO;
        
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.advertising attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1 constant:0]];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.advertising attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1 constant:0]];
        
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.advertising attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:0.75 constant:0]];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.advertising attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:0 constant:sizeName.height]];
        
    }
    
}


-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    //底部图片
    [self.bottomView showPromtpView];
    
}

-(void)viewWillDisappear:(BOOL)animated

{
    
    [super viewWillDisappear:animated];
    
}

#pragma mark ---- FaceCheck初始化
/**
 *  FaceCheck初始化
 */
- (void)createFacecheck{
    
    NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:[[NSBundle mainBundle] pathForResource:@"haarcascade_frontalface_alt2" ofType:@"xml"],PALivenessDetectorModelPath,[NSNumber numberWithFloat:kActionTime], PALivenessDetectorStepTimeLimit,self.number0fAction,PALivenessDetectorNumberOfAction,nil];
    /*!
     *  活体检测器类，检测均由此检测器完成
     */
    self.livenessDetector = [PALivenessDetector detectorOfOptions:options];
    [self.livenessDetector setDelegate:self];
    [self.livenessDetector reset];
    
}

/**
 *  活体动作提示界面
 */
- (void)initFaceActionView{
    
    //CGFloat height = kScaleHeight(150);
    
    self.bottomView = [[PABottomView alloc] init];
    [self.bottomView initWithTheCountDown:self.countDown andTheFaceConter:self.faceControl];
    self.bottomView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.bottomView setBackgroundColor:[UIColor clearColor]];
    [_FaceCheckBV addSubview:self.bottomView];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:_FaceCheckBV attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
}

/**
 *  识别提示倒计时
 */
- (void)createPAPromptView{
    
    promptView = [[PAPromptView alloc] initWithFrame:CGRectMake(kScaleWidth(20), kScaleHeight(80), kScreenWidth, kScaleHeight(200)) andTheSoundContor:self.soundContor];
    [promptView setBackgroundColor:[UIColor clearColor]];
    promptView.delegate = self;
    promptView.hidden = YES;
    [promptView showPrompt];
    [self.view addSubview:promptView];
    
}

#pragma mark - 进入活体检测回调
/**
 *  从宿主进入活体检测回调
 */
- (void)faceStartFinish
{
    
    //启动活体步骤
    [self startSession];
    //  开启活体检测
    [self starLivenessWithBuff];
    
    
}

- (void)showSendIngView{
    
    if (!sendView) {
        sendView = [[SendFailView alloc] init];
        sendView.delegate = self;
        [self.view addSubview:sendView];
    }
    [sendView startLoading];
}

#pragma mark --- 启动活体检测
/**
 *  开始倒计时完成回调
 */
- (void)promptViewFinish{
    
    [self createNoFaceContorTimeView:kActionTime];//不需要活体时的倒计时
    [self faceStartFinish];
}

/**
 *  启动提示动画
 */
- (void)startcheckFace{
    
    //动作提示动画及声音
    [self.bottomView closePromtpView];
    
    
}
#pragma mark --- 活体检测开启后，相应提示（界面）
/**
 *  开启活体检测（唯一入口，第一次进入后，转到“动作检测成功时”）
 */
-(void)starLivenessWithBuff{
    
    double delayInSeconds = 1.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
    dispatch_after(popTime,dispatch_get_main_queue(), ^(void){
        
        [self initHardCode:YES];
        [self startAnimation];
        
    });
    
}

/**
 *  停止活体检测步骤
 */
- (void)stopcheckFace{
    
    _starLiveness = NO;
    [promptView hidePrompt];
    [self.bottomView stopRollAnimation];
    
    if (self.session) {
        [self.session stopRunning];
        [self.session removeInput:self.videoInput];
        [self.session removeOutput:self.output];
        self.session = nil;
        if (self.videoInput) {
            self.videoInput = nil;
        }
        if (self.output) {
            self.output = nil;
        }
    }
    
    //停止语音
    [self playerStop];
}

/**
 *  开启相机
 */
- (void)startSession{
    if (self.session) {
        [self.session startRunning];
    }
}

/**
 *  加载相机图层预览
 */
- (void) setUpCameraLayer
{
    if (self.previewLayer == nil) {
        
        self.previewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:self.session];
        CALayer * viewLayer = [self.view layer];
        
        [self.previewLayer setFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
        [self.previewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
        
        [viewLayer insertSublayer:self.previewLayer below:[[viewLayer sublayers] objectAtIndex:0]];
        
    }
    
    [self.view bringSubviewToFront:self.bottomView];
    
}


/**
 *  动作提示动画及声音
 */
- (void)starAnimation:(PALivenessDetectionType)types{
    
    //换语音
    [self playerWith:types];
    //换图
    [self.bottomView willChangeAnimation:types outTime:kActionTime];
}

/**
 *  动作提示声音
 */
- (void)playerWith:(PALivenessDetectionType)types{
    
    switch(types){
        case DETECTION_TYPE_NONE:
        {
            //soundStr = @"2_eye";
            soundStr = @"缓慢眨眼";
            break;
        }
        case DETECTION_TYPE_MOUTH:
        {
            //soundStr = @"5_openMouth";
            soundStr = @"缓慢张嘴";
            break;
        }
        case DETECTION_TYPE_POS_YAW:
        {
            //soundStr = @"4_headshake";
            soundStr = @"缓慢摇头";
            break;
        }
        default:
        {
            return;
            break;
        }
    }
    [self playerStop];
    
    if (_curStep){
        
        //[self textToAudioWithStirng:@"非常好"];
        playerTimer = [NSTimer scheduledTimerWithTimeInterval:0 target:self selector:@selector(playSound) userInfo:nil repeats:NO];
    } else {
        [self playSound];
    }
}

/**
 *  播放活体动作类型声音
 */
- (void)playSound{
    //[self playAudioFilePath:kFaceFilePath(soundStr,@"wav")];
    [self textToAudioWithStirng:soundStr];
}

/**
 *  播放语音实现
 */
- (void)playAudioFilePath:(NSString *)filePath{
    if (!filePath) {
        return;
    }
    if (self.soundContor) {
        NSData *messageData = [NSData dataWithContentsOfFile:filePath];
        facePlayer = [[AVAudioPlayer alloc] initWithData:messageData error:nil];
        if (facePlayer) {
            facePlayer.delegate = self;
            [facePlayer play];
        }
        
    }
}
- (void)sendViewClickedButtonAtIndex:(NSInteger)index{}

/**
 *  Text转语音
 */
-(void)textToAudioWithStirng:(NSString*)textString{
    
    if (self.soundContor) {
        
        self.utterance = [AVSpeechUtterance speechUtteranceWithString:textString];
        //设置语言类别（不能被识别，返回值为nil） @"zh-CN"国语 @"zh-HK"粤语  @"zh-TW"台湾
        //self.voiceType = [AVSpeechSynthesisVoice voiceWithLanguage:@"zh-CN"];
        //self.utterance.voice = self.voiceType;
        //设置语速快慢
        self.utterance.rate = 0.4;
        //语音合成器会生成音频
        [self.synthesizer speakUtterance:self.utterance];
    }
    
}

/**
 *  返回
 */
- (void)backToAppAnimated:(BOOL)animated{
    
    [self stopcheckFace];
    
    if (self.timerCoundDown) {
        [self stopAnimations];
        
    }
    if (sendView) {
        [sendView stopLoading];
    }
    if (self.actionArray.count > 0) {
        [self.actionArray removeAllObjects];
    }
    
    if (self.livenessDetector) {
        [self.livenessDetector setDelegate:nil];
        self.livenessDetector = nil;
    }
    if (promptView) {
        
        [promptView removeFromSuperview];
        promptView = nil;
        [self.bottomView removeFromSuperview];
        self.bottomView = nil;
        personImage = nil;
        imageId = nil;
        self.actionArray = nil;
    }


}
#pragma mark ---- 初始化相机
/**
 *  初始化相机
 */
- (void) initialSession
{
    
    //判断是否允许用摄像头设备
    BOOL userAllow = [self toDetermineWhetherAuserAllowsCameraOperation];
    if (!userAllow) {
        
    }
    
    //1.创建会话层
    _session = [[AVCaptureSession alloc] init];
    [_session setSessionPreset:AVCaptureSessionPreset640x480];
    
    //2.创建、配置输入设备
    
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices)
    {
        if (device.position == AVCaptureDevicePositionFront)
        {
            self.videoInput = [AVCaptureDeviceInput deviceInputWithDevice:device error:nil];
        }
    }
    NSError *error;
    if (!self.videoInput)
    {
        NSLog(@"Error: %@", error);
        return;
    }
    [_session addInput:self.videoInput];
    
    ///out put
    AVCaptureVideoDataOutput *captureOutput = [[AVCaptureVideoDataOutput alloc]
                                               init];
    captureOutput.alwaysDiscardsLateVideoFrames = YES;
    //captureOutput.minFrameDuration = CMTimeMake(1, 10);
    
    dispatch_queue_t queue;
    queue = dispatch_queue_create("cameraQueue", NULL);
    [captureOutput setSampleBufferDelegate:self queue:queue];
    
    NSString* key = (NSString*)kCVPixelBufferPixelFormatTypeKey;
    NSNumber* value = [NSNumber
                       numberWithUnsignedInt:kCVPixelFormatType_32BGRA];
    NSDictionary* videoSettings = [NSDictionary
                                   dictionaryWithObject:value forKey:key];
    [captureOutput setVideoSettings:videoSettings];
    [_session addOutput:captureOutput];
    
    ///custom Layer
    self.customLayer = [CALayer layer];
    self.customLayer.frame = self.view.bounds;
    self.customLayer.transform = CATransform3DRotate(
                                                     CATransform3DIdentity, M_PI/2.0f, 0, 0, 1);
    self.customLayer.contentsGravity = kCAGravityResizeAspectFill;
    [self.view.layer addSublayer:self.customLayer];
    
    //3.创建、配置输出
    self.output = [[AVCaptureStillImageOutput alloc] init];
    NSDictionary *outputSettings = [[NSDictionary alloc] initWithObjectsAndKeys:AVVideoCodecJPEG,AVVideoCodecKey,nil];
    [self.output setOutputSettings:outputSettings];
    [_session addOutput:self.output];

    
    
    
};

#pragma mark --- 判断用户是否允许操作摄像头
-(BOOL)toDetermineWhetherAuserAllowsCameraOperation{
    
    
    NSString *mediaType = AVMediaTypeVideo;// Or AVMediaTypeAudio
    
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:mediaType];
    
    if(authStatus ==AVAuthorizationStatusRestricted){//拒绝访问
        
        //NSLog(@"拒绝访问");
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self checkFail:DETECTION_FAILED_TYPE_License];
            
        });
        return NO;
        
    }else if(authStatus == AVAuthorizationStatusDenied){
        
        //NSLog(@" 用户已经明确否认了这一照片数据的应用程序访问.");
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self checkFail:DETECTION_FAILED_TYPE_License];
            
        });
        return NO;
        
    }
    
    else if(authStatus == AVAuthorizationStatusAuthorized){//允许访问
        
        //NSLog(@"允许访问");
        return YES;
        
        
    }else if(authStatus == AVAuthorizationStatusNotDetermined){//不明确
        
        
        [AVCaptureDevice requestAccessForMediaType:mediaType completionHandler:^(BOOL granted) {
            
            if(granted){//点击允许访问时调用
                
                //NSLog(@"不明确,点击允许访问时调用");
            }
            
            else {
                
                //NSLog(@"不明确,点击不允许访问时调用");

                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    [self checkFail:DETECTION_FAILED_TYPE_License];
                    
                });
                
            }
            
        }];
        
    }
    return NO;
}


#pragma mark ----  初始化语音提示
/**
 *  初始化语音提示
 */
-(void)initAVSpeech{
    
    if (self.soundContor) {
        
        self.synthesizer = [[AVSpeechSynthesizer alloc] init];
        self.synthesizer.delegate = self;
        
    }
    
    
}
/**
 *  停止播放
 */
- (void)playerStop{
    if (playerTimer){
        [playerTimer invalidate];
        playerTimer = nil;
    }
    if (self.synthesizer.speaking) {
        [self.synthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    }
}


/**
 *  切换前后摄像头
 */
- (AVCaptureDevice *)cameraWithPosition:(AVCaptureDevicePosition) position {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices) {
        if ([device position] == position) {
            return device;
        }
    }
    return nil;
}

/**
 *  前置摄像头
 */
- (AVCaptureDevice *)frontCamera {
    return [self cameraWithPosition:AVCaptureDevicePositionFront];
}

/**
 *  后置摄像头
 */
- (AVCaptureDevice *)backCamera {
    return [self cameraWithPosition:AVCaptureDevicePositionBack];
}

//前后摄像头的切换
- (void)toggleCamera:(id)sender{
    NSUInteger cameraCount = [[AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo] count];
    if (cameraCount > 1) {
        NSError *error;
        AVCaptureDeviceInput *newVideoInput;
        AVCaptureDevicePosition position = [[_videoInput device] position];
        
        if (position == AVCaptureDevicePositionBack)
            newVideoInput = [[AVCaptureDeviceInput alloc] initWithDevice:[self frontCamera] error:&error];
        else if (position == AVCaptureDevicePositionFront)
            newVideoInput = [[AVCaptureDeviceInput alloc] initWithDevice:[self backCamera] error:&error];
        else
            return;
        
        if (newVideoInput != nil) {
            [self.session beginConfiguration];
            [self.session removeInput:self.videoInput];
            if ([self.session canAddInput:newVideoInput]) {
                [self.session addInput:newVideoInput];
                
                [self setVideoInput:newVideoInput];
                
            } else {
                [self.session addInput:self.videoInput];
            }
            [self.session commitConfiguration];
        } else if (error) {
            NSLog(@"toggle carema failed, error = %@", error);
        }
    }
}


/**
 * 设置
 */
- (void)initHardCode:(BOOL)check{
    _starLiveness = check;
    _curStep = 0;
}

#pragma mark - PromptDelegate
/**
 *  开始倒计时回调
 */
- (void)promptViewwillFinish{
    //停止指引语音提示
    [self playerStop];
}



#pragma mark - AVAudioPlayerDelegate
- (void)audioPlayerBeginInterruption:(AVAudioPlayer *)player{
    [player pause];
}

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag
{
    
}

//程序中断结束返回程序时，继续播放
- (void)audioPlayerEndInterruption:(AVAudioPlayer *)player
{
    [player play];
}

#pragma mark - AVCaptureVideoDataOutputSampleBufferDelegate
- (void)captureOutput:(AVCaptureOutput *)captureOutput
didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer
       fromConnection:(AVCaptureConnection *)connection
{
    if (_starLiveness) {
        
        [self.livenessDetector detectWithBuffer:sampleBuffer orientation:UIImageOrientationRight];
        
    }
}

#pragma mark - PALivenessProtocolDelegate
/**
 *  @brief  倒计时
 *
 *  @return 剩余时间
 */
-(void)theCounterDownWithCurrent:(int)currentTime{
    
    MAIN_ACTION((^(){
        self.numLabel.text = [NSString stringWithFormat:@"%ld",kActionTime - (long)currentTime +1];
    }));
    
}
#pragma mark - 检测过程中对于用户行为的建议

/**
 *  @brief  检测过程中对于用户行为的建议
 *
 *  @param testType 用户行为错误类型
 */

-(void)onFrameDetectedForTheUserBehaviorInTheProcessOfTesting:(EnvironmentalErrorEnum)testType{
    
    MAIN_ACTION((^{
        [self.bottomView promptLabelText:testType];
    }));
    
}
-(void)onDetectionRect:(CGRect)rect andTheImg:(UIImage*)image andThePoint2f:(NSArray*)point2f
{
    //[self performSelectorOnMainThread:@selector(detectForFacesInUIImage:)
    //                      withObject: (id) image waitUntilDone:NO];
    
}
#pragma mark - 检脸成功，进入动作环节

/**
 *  @brief  动作检测
 *
 *  @param animationType 用户动作类型
 */
-(void)onDetectionChangeAnimation:(PALivenessDetectionType)animationType{
    
    MAIN_ACTION((^{
        [self stopAnimations];
        [self.bottomView closePromtpView];
        [self starAnimation:animationType];
    }));
    
}

- (IBAction)turnCamera:(id)sender {
    
    NSArray *inputs = _session.inputs;
    for ( AVCaptureDeviceInput *input in inputs )
    {
        AVCaptureDevice *device = input.device;
        if ([device hasMediaType:AVMediaTypeVideo])
        {
            AVCaptureDevicePosition position = device.position;
            AVCaptureDevice *newCamera = nil;
            AVCaptureDeviceInput *newInput = nil;
            
            if (position == AVCaptureDevicePositionFront)
            {
                newCamera = [self cameraWithPosition:AVCaptureDevicePositionBack];
            }
            else
            {
                newCamera = [self cameraWithPosition:AVCaptureDevicePositionFront];
            }
            self.device = newCamera;
            newInput = [AVCaptureDeviceInput deviceInputWithDevice:newCamera error:nil];
            
            // beginConfiguration ensures that pending changes are not applied immediately
            [_session beginConfiguration];
            
            [_session removeInput:input];
            [_session addInput:newInput];
            
            // Changes take effect once the outermost commitConfiguration is invoked.
            [_session commitConfiguration];
            break;
        }
    }
}



#pragma mark - 当前活体检测的动作检测失败时，调用此方法。
/*!
 *  当前活体检测的动作检测失败时，调用此方法。
 *
 *  @param failedType 动作检测失败的原因（具体选项请参照PALivenessDetectionFailedType）
 *  @return 无
 */
-(void)onDetectionFailed:(PALivenessDetectionFailedType)failedType{
    
    MAIN_ACTION((^(){
        [self stopcheckFace];
        [self stopAnimations];
        [self showSendIngView];
        [self checkFail:failedType];
    }));
}
#pragma mark - 检测成功时，调用此方法。
/*!
 *  当前活体检测成功时，调用此方法。
 */
- (void)onDetectionSuccess:(PALivenessDetectionFrame *)faceInfo{
    
    MAIN_ACTION(^()
                {
                    
                    //质量评估
                    if ([self qualityCheck]) {
                        
                        //停止活体检测步骤
                        [self stopcheckFace];
                        [self showSendIngView];
                        [self sendImage:faceInfo];
                        
                    } else {
                        //失败 统一处理
                        
                        [self stopcheckFace];
                        [self stopAnimations];
                    }
                });
}
#pragma mark ---  质量评估
/**
 *  质量评估
 */
- (BOOL)qualityCheck{
    
    //    for (PALivenessDetectionFrame *imageInfo in [self.livenessDetector getValidFrame]){
    //
    //        if (imageInfo.attr.quality <= thresholdValue && imageInfo.attr.blurness_motion > fuzzyValue) {
    //
    //            return NO;
    //
    //        }
    //
    //        break;
    //    }
    
    return YES;
}


#pragma mark ---  质量评估失败
/**
 *  质量评估失败
 */
- (void)checkFail:(PALivenessDetectionFailedType)failedType{
    
    MAIN_ACTION((^(){
        
        [self initHardCode:NO];
        [self stopcheckFace];
        [self showSendIngView];
        
        [self backToAppAnimated:YES];
        
        if (self.navigationController && self.navigationController.viewControllers.count > 1) {
            
            [self.navigationController popViewControllerAnimated:NO];
            
        } else {
            
            [self dismissViewControllerAnimated:NO completion:^{
                
                if (self.delegate && [self.delegate respondsToSelector:@selector(getSinglePAcheckReport:error:andThePAFaceCheckdelegate:)]){
                    
                    NSDictionary *dict = @{@"failedType":[NSString stringWithFormat:@"%u",failedType],
                                           };
                    
                    NSString *errorDomain = @"";
                    if (failedType == 0) {
                        errorDomain = @"DETECTION_FAILED_TYPE_ACTIONBLEND";
                    }
                    if (failedType == 1) {
                        errorDomain = @"DETECTION_FAILED_TYPE_DiscontinuityAttack";
                    }
                    if (failedType == 2) {
                        errorDomain = @"DETECTION_FAILED_TYPE_NOTVIDEO";
                    }
                    if (failedType == 3) {
                        errorDomain = @"DETECTION_FAILED_TYPE_TIMEOUT";
                    }
                    if (failedType == 4) {
                        errorDomain = @"DETECTION_FAILED_TYPE_Interrupt";
                    }
                    if (failedType == 5) {
                        errorDomain = @"DETECTION_FAILED_TYPE_License";
                    }
                    
                    NSError *aError = [NSError errorWithDomain:errorDomain code:failedType userInfo:dict];
                    [self.delegate getSinglePAcheckReport:dict error:aError andThePAFaceCheckdelegate:self];
                    
                }
                
                
            }];
        }


        
    }));
}

#pragma mark - PAcheckFailViewDelegate
/**
 *  结果页返回回调
 */
- (void)recheck{
    [self playerStop];
    
    if (_curStep == self.currentStep){
        [self backToAppAnimated:YES];
    }
    else {
        [self startcheckFace];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    [sendView stopLoading];
}


#pragma mark --- 转屏
/**
 *  ios6下禁止横屏
 */
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation{
    
    if (toInterfaceOrientation == UIInterfaceOrientationLandscapeLeft) {
        [self setUpCameraLayer];
    }
    return NO;
}

/**
 *  ios7以上禁止横屏
 */
- (BOOL)shouldAutorotate{
    
    return YES;
}

- (void)dealloc {
    //NSLog(@" PAFacecheck dealloc");
}


@end
