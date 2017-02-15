//
//  PromptView.m
//  PAFacecheckController
//
//  Created by ken on 15/4/22.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "PAPromptView.h"
#import "PAZCLDefineTool.h"

@interface PAPromptView ()
{
    UIImageView *timeView;
    UILabel     *label;
    UIImageView *imageView;
    
    NSTimer     *showTimer;
    NSTimer     *changeTimer;
    int         kSoundTime;
}
@end




@implementation PAPromptView

-(id)initWithFrame:(CGRect)frame andTheSoundContor:(BOOL)soundContr
{
    self = [super initWithFrame:frame];
    if (self) {
        if (soundContr) {
            kSoundTime = 1;
        }else{
            
            kSoundTime = 1;
        }
        
        
        self.backgroundColor = [UIColor clearColor];
        UIImage *bgImage = kFaceImage(@"Face_labelBG");
        
        //倒计时
        timeView = [[UIImageView alloc] init];
        timeView.frame = CGRectMake(0, 0, bgImage.size.width, bgImage.size.height);
        timeView.backgroundColor = [UIColor clearColor];
        timeView.userInteractionEnabled = YES;
        [self addSubview:timeView];
        
        //倒计时（3S）
        UIImage *image = kFaceImage(@"Face_timeStart");
        imageView = [[UIImageView alloc] initWithImage:image];
        [imageView setFrame:CGRectMake(0, 0, kScaleWidth(image.size.width), kScaleWidth(image.size.height))];
        imageView.center = CGPointMake(timeView.frame.size.width/2, frame.size.height/2+kScaleHeight(55));
        //[timeView addSubview:imageView];
        
        label = [[UILabel alloc] init];
        label.backgroundColor = [UIColor clearColor];
        label.text = @"3";
        label.font = [UIFont boldSystemFontOfSize:kScaleWidth(63)];
        label.frame = imageView.frame;
        label.textColor = [UIColor whiteColor];
        label.textAlignment = NSTextAlignmentCenter;
        //[timeView addSubview:label];
        
    }
    return self;
}
#pragma mark -- 11S的倒计时（不跳过指引）--请保持。。。。。。。
- (void)showPrompt{
    timeView.hidden = YES;
    //    promptView.hidden = NO;
    self.hidden = NO;
    //showTimer = [NSTimer scheduledTimerWithTimeInterval:kSoundTime target:self selector:@selector(startTimer) userInfo:nil repeats:NO];
    [self startTimer];
}

//隐藏提示界面
- (void)hidePrompt{
    self.hidden = YES;
    //    skipBut.hidden = NO;
    
    //关闭11s倒计时
    [self closeShowTimer];
    //关闭3s倒计时
    [self closeChangeTimer];
}

#pragma mark -- 隐藏跳过按钮
- (void)reSet{
    //    skipBut.hidden = YES;
}

- (void)closeChangeTimer{
    if (changeTimer){
        [changeTimer invalidate];
        changeTimer = nil;
    }
}

- (void)closeShowTimer{
    if (showTimer){
        [showTimer invalidate];
        showTimer = nil;
    }
}

#pragma mark -- 倒计时，进入3S倒计时
- (void)startTimer{
    
    //关闭11S倒计时，进入3S倒计时
    [self closeShowTimer];
    
    label.text = @"2";
    //    promptView.hidden = YES;
    timeView.hidden = NO;
    
    //loading动画(2S)
    CABasicAnimation* rotationAnimation;
    rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0];
    rotationAnimation.duration = 1.0f;
    rotationAnimation.cumulative = YES;
    rotationAnimation.repeatCount = 2;
    [imageView.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
    
    //开始倒计时（2S）
    changeTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(stopTimeView) userInfo:nil repeats:YES];
}

#pragma mark -- 开始倒计时（2S）
- (void)stopTimeView{
    NSInteger num = [label.text integerValue];
    num--;
    if (num) {
        label.text = [NSString stringWithFormat:@"%d", (int)num];
    } else {
        [self closeChangeTimer];
        [imageView.layer removeAllAnimations];
        self.hidden = YES;
        if (self.delegate && [self.delegate respondsToSelector:@selector(promptViewFinish)]){
            //开启活体检测（唯一入口）
            [self.delegate promptViewFinish];
        }
    }
}

//- (void)dealloc {
//    NSLog(@" prompt dealloc");
//}

@end
