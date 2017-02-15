//
//  BottomView.h
//  PAFacecheckController
//
//  Created by ken on 15/4/27.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PALivenessDetector.h"

typedef NS_ENUM(NSInteger, animationState){
    animationOne,
    animationTwo,
    animationThree,
};

@interface PABottomView : UIView

- (void)willChangeAnimation:(PALivenessDetectionType)state outTime:(CGFloat)time;
-(void)initWithTheCountDown:(BOOL)countDown andTheFaceConter:(BOOL)faceConter;
- (void)recovery;
- (void)showPromtpView;
- (void)closePromtpView;
- (void)startRollAnimation;
- (void)stopRollAnimation;
- (void)promptLabelText:(EnvironmentalErrorEnum)promptLabelStr;
@end
