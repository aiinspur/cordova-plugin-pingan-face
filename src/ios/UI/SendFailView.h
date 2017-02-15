//
//  SendFailView.h
//  PAFacecheckController
//
//  Created by ken on 15/4/30.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SendFailView;

@protocol SendViewDelegate <NSObject>
- (void)sendViewClickedButtonAtIndex:(NSInteger)index;
@end

@interface SendFailView : UIView

@property (nonatomic, strong) id <SendViewDelegate> delegate;

@property (nonatomic, strong) UILabel *labelText;
- (void)startLoading;
- (void)stopLoading;
- (void)loadFail:(NSString *)str;
@end
