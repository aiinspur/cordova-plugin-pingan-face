//
//  PromptView.h
//  PAFacecheckController
//
//  Created by ken on 15/4/22.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PAPromptView;

@protocol PAPromptDelegate <NSObject>
- (void)promptViewFinish;
- (void)promptViewwillFinish;

@end

@interface PAPromptView : UIView

@property (nonatomic, strong) id <PAPromptDelegate>delegate;
-(id)initWithFrame:(CGRect)frame andTheSoundContor:(BOOL)soundContr;
- (void)showPrompt;
- (void)hidePrompt;
- (void)reSet;

@end
