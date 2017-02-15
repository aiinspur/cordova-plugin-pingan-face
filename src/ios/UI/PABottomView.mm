//
//  BottomView.m
//  PAFacecheckController
//
//  Created by ken on 15/4/27.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "PABottomView.h"
#import "PACircularRing.h"
#import "PAZCLDefineTool.h"

@interface PABottomView ()
{
    CGFloat _aniViewHeigh;
    CGFloat _aniViewWidth;
    BOOL _stopAnimaiton;
    UIView *imageBackGround;
    
    UILabel *messageLabelA;
    //loading动画
    PACircularRing *rightRingA;
    UIImageView *imageA;
    
    UILabel *messageLabelB;
    PACircularRing *rightRingB;
    UIImageView *imageB;
    
    BOOL isFirst;
    
    UILabel *promptLabel;
    
}
@property (nonatomic, strong) UIImageView *imageViewA;
@property (nonatomic, strong) UIImageView *imageViewB;
@property (nonatomic, assign) CGFloat cencerX;
@property (nonatomic) BOOL countDown;
@property (nonatomic) BOOL faceConter;

@end

#define kTopDis kScaleWidth(30)

@implementation PABottomView

-(void)initWithTheCountDown:(BOOL)countDown andTheFaceConter:(BOOL)faceConter{
    
    self.countDown = countDown;
    
    self.imageViewA = [[UIImageView alloc] init];
    self.imageViewB = [[UIImageView alloc] init];
    
    
    self.imageViewA.translatesAutoresizingMaskIntoConstraints = NO;
    self.imageViewB.translatesAutoresizingMaskIntoConstraints = NO;
    

    [self addSubview:self.imageViewA];
    [self addSubview:self.imageViewB];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    
    _aniViewWidth = kScaleWidth(80);
    _aniViewHeigh = kScaleHeight(80);
    _cencerX = kScaleWidth(160)-_aniViewWidth/2;
    
    imageA = [[UIImageView alloc] init];
    imageB = [[UIImageView alloc] init];
    
    imageA.translatesAutoresizingMaskIntoConstraints = NO;
    imageB.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self.imageViewA addSubview:imageA];
    [self.imageViewB addSubview:imageB];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageA attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageA attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageA attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeHeight multiplier:.7 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageA attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeHeight multiplier:.7 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageB attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageB attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageB attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeHeight multiplier:.7 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageB attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeHeight multiplier:.7 constant:0]];
    
    //倒计时

    rightRingA = [[PACircularRing alloc] init];
    rightRingB = [[PACircularRing alloc] init];
    
    if (countDown) {
        
        rightRingA.translatesAutoresizingMaskIntoConstraints = NO;
        rightRingB.translatesAutoresizingMaskIntoConstraints = NO;
        
        [self.imageViewA addSubview:rightRingA];
        [self.imageViewB addSubview:rightRingB];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingA attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeRight multiplier:0.9 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingA attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingA attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeHeight multiplier:0.3 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingA attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.imageViewA attribute:NSLayoutAttributeHeight multiplier:0.3 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingB attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeRight multiplier:0.9 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingB attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingB attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeHeight multiplier:0.3 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:rightRingB attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.imageViewB attribute:NSLayoutAttributeHeight multiplier:0.3 constant:0]];
        
    }
    
    self.faceConter = faceConter;
    
    //动作文字提示
    //messageLabelA = [[UILabel alloc] initWithFrame:CGRectMake(kScaleWidth(200), kScaleHeight(80),kScaleWidth(100), 30)];
    messageLabelA = [[UILabel alloc]init];
    [messageLabelA setFont:[UIFont systemFontOfSize:kScaleWidth(18)]];
    [messageLabelA setTextColor:kTextColor];
    [messageLabelA setTextAlignment:NSTextAlignmentLeft];
    [messageLabelA setBackgroundColor:[UIColor clearColor]];
    
    //messageLabelB = [[UILabel alloc] initWithFrame:messageLabelA.frame];
    messageLabelB = [[UILabel alloc]init];
    [messageLabelB setFont:[UIFont systemFontOfSize:kScaleWidth(18)]];
    [messageLabelB setTextColor:kTextColor];
    [messageLabelB setTextAlignment:NSTextAlignmentLeft];
    [messageLabelB setBackgroundColor:[UIColor clearColor]];
    
    messageLabelA.translatesAutoresizingMaskIntoConstraints = NO;
    messageLabelB.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self.imageViewA addSubview:messageLabelA];
    [self.imageViewB addSubview:messageLabelB];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:messageLabelA attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:imageA attribute:NSLayoutAttributeRight multiplier:1 constant:10]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:messageLabelA attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:imageA attribute:NSLayoutAttributeBottom multiplier:1 constant:0]];
    
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:messageLabelB attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:imageB attribute:NSLayoutAttributeRight multiplier:1 constant:10]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:messageLabelB attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:imageB attribute:NSLayoutAttributeBottom multiplier:1 constant:0]];
    
    
    
    
}


#pragma mark --- 提示菜单
- (void)showPromtpView{
    
    [self recovery];
    
    if (!imageBackGround){
        
        //imageBackGround = [[UIView alloc] initWithFrame:CGRectMake(0, kScaleHeight(37), kScreenWidth, kScaleHeight(50))];
        imageBackGround = [[UIView alloc]init];
        imageBackGround.translatesAutoresizingMaskIntoConstraints = NO;
        [imageBackGround setBackgroundColor:[UIColor clearColor]];
        [self addSubview:imageBackGround];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:imageBackGround attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:imageBackGround attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:imageBackGround attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:imageBackGround attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
        
        //默认
        promptLabel = [[UILabel alloc] init];
        promptLabel.translatesAutoresizingMaskIntoConstraints = NO;
        [promptLabel setBackgroundColor:[UIColor clearColor]];
        promptLabel.textColor = [UIColor blackColor];
        promptLabel.textAlignment = NSTextAlignmentCenter;
        [imageBackGround addSubview:promptLabel];
        
        if (self.countDown) {
            promptLabel.font = [UIFont boldSystemFontOfSize:kScaleWidth(22)];
            
        }else{
            promptLabel.font = [UIFont boldSystemFontOfSize:kScaleWidth(25)];
            
        }
        
        promptLabel.text = @"请正对摄像头";
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:promptLabel attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:imageBackGround attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:promptLabel attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:imageBackGround attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:promptLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:imageBackGround attribute:NSLayoutAttributeHeight multiplier:0.9 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:promptLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:imageBackGround attribute:NSLayoutAttributeWidth multiplier:0.5 constant:0]];
    }
}

- (void)promptLabelText:(EnvironmentalErrorEnum)promptLabelStr{
    
    
    switch (promptLabelStr) {
        case 0:
            promptLabel.text = @"请稍微退后";
            
            break;
        case 1:
            promptLabel.text = @"请稍微靠近";
            break;
            
        case 2:
            promptLabel.text = @"环境光线太强";
            break;
            
        case 3:
            promptLabel.text = @"环境光线太暗";
            break;
            
        case 4:
            promptLabel.text = @"请正对摄像头";
            break;
            
        case 5:
            promptLabel.text = @"请正对摄像头";
            break;
            
        case 6:
            promptLabel.text = @"图片过于模糊";
            break;
        case 7:
            promptLabel.text = @"请保持相对静止";
            break;
        case 8:
            promptLabel.text = @"采集框存在多人";
            break;
        case 9:
            promptLabel.text = @"检测中";
            break;
            
        default:
            break;
    }
    
}

- (void)closePromtpView{
    [imageBackGround removeFromSuperview];
    imageBackGround = nil;
}

- (void)recovery{
    
    _stopAnimaiton = YES;
    isFirst= YES;
    
    [rightRingA stopAnimation];
    [rightRingB stopAnimation];
    
    [imageA stopAnimating];
    [imageB stopAnimating];
    imageA.animationImages = nil;
    imageB.animationImages = nil;
    [imageA setImage:nil];
    [imageB setImage:nil];
    
    //self.imageViewA.frame = CGRectMake(0, 0, kScreenWidth, self.frame.size.height);
    //self.imageViewB.frame = CGRectMake(kScreenWidth, 0, kScreenWidth, self.frame.size.height);
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewA attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.imageViewB attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    
    [messageLabelA setText:@""];
    [messageLabelB setText:@""];
}

- (void)willChangeAnimation:(PALivenessDetectionType)state outTime:(CGFloat)time{
        
    [imageA stopAnimating];
    _stopAnimaiton = NO;
    [rightRingA stopAnimation];
    //重置时间
    [rightRingB setMaxTime:time];
    
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:2];
    NSString *title = nil;
    
    
    switch (state) {
            
        case DETECTION_TYPE_MOUTH:
        {
            [array addObject:kFaceImage(@"Face_head_mouse")];
            [array addObject:kFaceImage(@"Face_head_blink")];
            title = @"缓慢张嘴";
            break;
        }
        case DETECTION_TYPE_POS_YAW:
        {
            [array addObject:kFaceImage(@"Face_head_left")];
            [array addObject:kFaceImage(@"Face_head_blink")];
            [array addObject:kFaceImage(@"Face_head_right")];
            [array addObject:kFaceImage(@"Face_head_blink")];
            title = @"缓慢摇头";
            break;
        }
            default:{
            title = @"";
        }
            break;
    }
    
    if (array.count) {
        CGRect sourceRect = CGRectMake(0, 0, kScreenWidth, self.frame.size.height);
        CGRect leftHideRect = CGRectMake(-kScreenWidth, 0, kScreenWidth, self.frame.size.height);
        CGRect rightHideRect = CGRectMake(kScreenWidth, 0, kScreenWidth, self.frame.size.height);
        
        //信息Title
        messageLabelA.text = title;
        messageLabelB.text = title;
        
        imageB.image = array[0];
        
        if (isFirst) {
            isFirst = NO;
            [rightRingA setMaxTime:time];
            [rightRingA startAnimation];
            
            imageA.image = imageB.image;
            
            [self.imageViewA setFrame:sourceRect];
            [self.imageViewB setFrame:rightHideRect];
            
            [imageA setAnimationImages:array];
            [imageA setAnimationRepeatCount:999];
            [imageA setAnimationDuration:1.5f];
            [imageA startAnimating];
        } else {
            [UIView animateWithDuration:0.3f
                             animations:^{
                                 if (!_stopAnimaiton) {
                                     [self.imageViewA setFrame:leftHideRect];
                                     [self.imageViewB setFrame:sourceRect];
                                 }else{
                                     [imageA setImage:kFaceImage(@"Face_head_blink")];
                                 }
                             }
                             completion:^(BOOL finished) {
                                 
                                 [rightRingA setMaxTime:time];
                                 [rightRingA startAnimation];
                                 
                                 if (!_stopAnimaiton) {
                                     imageA.image = imageB.image;
                                     
                                     [self.imageViewA setFrame:sourceRect];
                                     [self.imageViewB setFrame:rightHideRect];
                                     
                                     [imageA setAnimationImages:array];
                                     
                                     [imageA setAnimationRepeatCount:999];
                                     [imageA setAnimationDuration:1.f];
                                     
                                     [imageA startAnimating];
                                     
                                 }else{
                                     imageA.animationImages = nil;
                                     
                                     [self.imageViewA setFrame:sourceRect];
                                     [self.imageViewB setFrame:rightHideRect];
                                     [imageA setImage:kFaceImage(@"Face_head_blin")];
                                 }
                             }];
        }
    }
}

- (void)startRollAnimation{
    [rightRingA startAnimation];
}

- (void)stopRollAnimation{
    _stopAnimaiton = YES;
    
    [imageA stopAnimating];
    [imageB stopAnimating];
    
    [rightRingA stopAnimation];
    [rightRingB stopAnimation];
}

- (void)dealloc {
    NSLog(@" bottom dealloc");
}

@end
