//
//  SendFailView.m
//  PAFacecheckController
//
//  Created by ken on 15/4/30.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "SendFailView.h"
#import "PAZCLDefineTool.h"

@interface SendFailView ()
{
    UIActivityIndicatorView *activityIndicator;
    UIImageView *alterBg;

}
@end

@implementation SendFailView

- (id)init{
    
    self = [super init];
    if (self){
        
        self.frame = CGRectMake(0, 0, kScreenWidth, kScreenHeight);
        self.backgroundColor = [UIColor colorWithRed:0. green:0. blue:0. alpha:0.8];
        self.userInteractionEnabled = YES;
        
        activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        activityIndicator.center = CGPointMake(self.center.x, kScaleHeight(210));
        activityIndicator.color = [UIColor whiteColor];
        [activityIndicator setHidesWhenStopped:YES];
        [self addSubview:activityIndicator];

        
        self.labelText = [[UILabel alloc] init];
        self.labelText.frame = CGRectMake(0, kScaleHeight(210)+40, kScreenWidth, 40);
        self.labelText.textAlignment = NSTextAlignmentCenter;
        self.labelText.text = @"正在获取检测结果，请稍候……";
        self.labelText.font = [UIFont systemFontOfSize:18];
        self.labelText.textColor = [UIColor whiteColor];
        self.labelText.backgroundColor = [UIColor clearColor];
        [self addSubview:self.labelText];
        
    }
    return self;
}

- (void)startLoading{
    if (alterBg){
        alterBg.hidden = YES;
    }
    self.hidden = NO;
    self.labelText.hidden = NO;
    [activityIndicator startAnimating];

}

- (void)stopLoading{
    self.hidden = YES;
    [activityIndicator stopAnimating];
    self.labelText.hidden = YES;

}


- (void)loadFail:(NSString *)str{
    [activityIndicator stopAnimating];
    self.labelText.hidden = YES;

    if (!alterBg)
    {
        [self createFailView:str];
    } else {
        alterBg.hidden = NO;
    }
}

- (void)createFailView:(NSString *)str{
    UIImage *image = kFaceImage(@"Face_alterBg");
    alterBg = [[UIImageView alloc] init];
    alterBg.frame = CGRectMake(kScaleWidth(40), kScaleHeight(150), kScaleWidth(240), kScaleHeight(140));
    alterBg.image = image;
    alterBg.userInteractionEnabled = YES;
    [self addSubview:alterBg];
    
    UILabel *labelTitle = [[UILabel alloc] init];
    labelTitle.frame = CGRectMake(0, kScaleHeight(15), alterBg.frame.size.width, kScaleHeight(25));
    labelTitle.font = [UIFont boldSystemFontOfSize:kScaleWidth(19)];
    labelTitle.textColor = [UIColor blackColor];
    labelTitle.backgroundColor = [UIColor clearColor];
    labelTitle.textAlignment = NSTextAlignmentCenter;
    labelTitle.text = @"温馨提示";
    [alterBg addSubview:labelTitle];
    
    UILabel *labelMes = [[UILabel alloc] init];
    labelMes.frame = CGRectMake(0, kScaleHeight(10)+labelTitle.frame.size.height+labelTitle.frame.origin.y, alterBg.frame.size.width, kScaleHeight(25));
    labelMes.font = [UIFont systemFontOfSize:kScaleWidth(15)];
    labelMes.textColor = [UIColor blackColor];
    labelMes.backgroundColor = [UIColor clearColor];
    labelMes.textAlignment = NSTextAlignmentCenter;
    labelMes.text = str;
    [alterBg addSubview:labelMes];
    
    UIImage *butImage = kFaceImage(@"Face_alterBut_N");
    UIImage *butHeightLine = kFaceImage(@"Face_alterBut_S");
    
    UIButton *but1 = [UIButton buttonWithType:UIButtonTypeCustom];
    but1.titleLabel.font = [UIFont systemFontOfSize:kScaleWidth(15)];
    but1.frame = CGRectMake(kScaleWidth(32), labelMes.frame.origin.y +labelMes.frame.size.height + kScaleHeight(15), kScaleWidth(75), kScaleHeight(30));
    [but1 setBackgroundImage:butImage forState:UIControlStateNormal];
    [but1 setBackgroundImage:butHeightLine forState:UIControlStateHighlighted];
    [but1 setTitle:@"取消" forState:UIControlStateNormal];
    [but1 setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [but1 addTarget:self action:@selector(cancel) forControlEvents:UIControlEventTouchUpInside];
    [alterBg addSubview:but1];
    
    UIButton *but2 = [UIButton buttonWithType:UIButtonTypeCustom];
    but2.frame = CGRectMake(but1.frame.origin.x +but1.frame.size.width +kScaleWidth(26), but1.frame.origin.y, but1.frame.size.width, but1.frame.size.height);
    but2.titleLabel.font = [UIFont systemFontOfSize:kScaleWidth(15)];
    [but2 setBackgroundImage:butImage forState:UIControlStateNormal];
    [but2 setBackgroundImage:butHeightLine forState:UIControlStateHighlighted];
    [but2 setTitle:@"重试" forState:UIControlStateNormal];
    [but2 setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [but2 addTarget:self action:@selector(resend) forControlEvents:UIControlEventTouchUpInside];
    [alterBg addSubview:but2];

}

- (void)resend{
    [self startLoading];
    

    if (self.delegate &&[self.delegate respondsToSelector:@selector(sendViewClickedButtonAtIndex:)]) {
        [self.delegate sendViewClickedButtonAtIndex:1];
    }

}

- (void)cancel{
    [self stopLoading];

    if (self.delegate &&[self.delegate respondsToSelector:@selector(sendViewClickedButtonAtIndex:)]) {
        [self.delegate sendViewClickedButtonAtIndex:0];
    }
}

@end
