//
//  PACircularRing.h
//  PAFacecheckController
//
//  Created by ken on 15/4/27.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PACircularRing : UIView
{
    NSInteger _maxTime;
    NSInteger _count;
}

@property (nonatomic, strong) UIImageView *bottomView;
@property (nonatomic, strong) UILabel *numLabel;
@property (nonatomic, strong) NSTimer *timer;


- (void)setMaxTime:(CGFloat)time;
- (void)startAnimation;
- (void)stopAnimation;

@end
