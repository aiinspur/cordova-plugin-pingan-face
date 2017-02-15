//
//  PACircularRing.m
//  PAFacecheckController
//
//  Created by ken on 15/4/27.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "PACircularRing.h"
#import "PAZCLDefineTool.h"

@implementation PACircularRing

-(instancetype)init{

    self = [super init];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        //进度倒计时(10)
        self.bottomView = [[UIImageView alloc] initWithImage:kFaceImage(@"Face_timeAnimation")];
        CGFloat lineWidth = 4.f;
        CGFloat radius = CGRectGetWidth(self.bounds)/2 - lineWidth/2;
        CGRect rect = CGRectMake(lineWidth/2, lineWidth/2, radius * 2, radius * 2);
        
        lineWidth = 3.f;
        radius = radius - 1;
        rect = CGRectMake(lineWidth/2+1, lineWidth/2+1, radius * 2, radius * 2);
        //[self.bottomView setFrame:rect];
        
        //self.numLabel = [[UILabel alloc] initWithFrame:self.bounds];
        self.numLabel = [[UILabel alloc] init];
        [self.numLabel setTextAlignment:NSTextAlignmentCenter];
        [self.numLabel setFont:[UIFont systemFontOfSize:kScaleWidth(18)]];
        [self.numLabel setTextColor:kTextColor];
        [self.numLabel setBackgroundColor:[UIColor clearColor]];
        
        [self addSubview:self.bottomView];
        [self addSubview:self.numLabel];
        
        self.bottomView.translatesAutoresizingMaskIntoConstraints = NO;
        self.numLabel.translatesAutoresizingMaskIntoConstraints = NO;
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterX multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.numLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];

        [self setHidden:YES];

    }
    return self;
}

//- (instancetype)initWithFrame:(CGRect)frame{
//    self = [super initWithFrame:frame];
//    if (self) {
//        self.backgroundColor = [UIColor clearColor];
//        
//        //进度倒计时(10)
//        self.bottomView = [[UIImageView alloc] initWithImage:kFaceImage(@"Face_timeAnimation")];
//        CGFloat lineWidth = 4.f;
//        CGFloat radius = CGRectGetWidth(self.bounds)/2 - lineWidth/2;
//        CGRect rect = CGRectMake(lineWidth/2, lineWidth/2, radius * 2, radius * 2);
//
//        lineWidth = 3.f;
//        radius = radius - 1;
//        rect = CGRectMake(lineWidth/2+1, lineWidth/2+1, radius * 2, radius * 2);
//        [self.bottomView setFrame:rect];
//        
//        self.numLabel = [[UILabel alloc] initWithFrame:self.bounds];
//        [self.numLabel setTextAlignment:NSTextAlignmentCenter];
//        [self.numLabel setFont:[UIFont systemFontOfSize:kScaleWidth(18)]];
//        [self.numLabel setTextColor:kTextColor];
//        [self.numLabel setBackgroundColor:[UIColor clearColor]];
//        
//        [self addSubview:self.bottomView];
//        [self addSubview:self.numLabel];
//        
//       
//        [self setHidden:YES];
//    }
//    return self;
//}

- (void)setMaxTime:(CGFloat)time{
    if (self.timer) {
        [self.timer invalidate];
        self.timer = nil;
    }
    
    _count = 0;
    _maxTime = time;
    

    self.numLabel.text = [NSString stringWithFormat:@"%ld", (long)_maxTime];

}

- (void)startAnimation{
    [self setHidden:NO];
    
//    MAIN_ACTION((^(){
        [self.numLabel setText:[NSString stringWithFormat:@"%ld", (long)_maxTime]];
//    }));

    if (self.timer == nil) {
        self.timer = [NSTimer timerWithTimeInterval:1 target:self selector:@selector(timeChange) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:self.timer forMode:NSDefaultRunLoopMode];
    }
    
    [self.timer fire];

    if (_maxTime == 0) {
        [self.bottomView.layer removeAllAnimations];

    }else{
        CABasicAnimation* rotationAnimation;
        rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
        rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0];
        rotationAnimation.duration = 1.0f;
        rotationAnimation.cumulative = YES;
        rotationAnimation.repeatCount = kActionTime;
        [self.bottomView.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
    }
    
}

- (void)stopAnimation{
    
    [self.bottomView.layer removeAllAnimations];
    
    if (self.timer) {
        [self.timer invalidate];
        self.timer = nil;
    }
}

- (void)timeChange{
    _count++;
    
    NSInteger num = _maxTime - _count;
    [self.numLabel setText:[NSString stringWithFormat:@"%ld", (long)num +1]];

    
    if (_count == _maxTime) {
//        [self.numLabel setText:[NSString stringWithFormat:@"%ld", (long)_maxTime]];
        [self.timer invalidate];
        self.timer = nil;
    }
}





@end
