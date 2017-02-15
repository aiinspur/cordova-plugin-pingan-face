//
//  PAZCLTools.h
//  PAFaceCheck
//
//  Created by prliu on 16/3/22.
//  Copyright © 2016年 pingan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "PAZCLDefineTool.h"

@interface PAZCLTools : NSObject

//+ (ZCLTools *)share;
+ (UIBarButtonItem *)createRightBarButtonItem:(NSString *)title target:(id)obj selector:(SEL)selector ImageName:(NSString*)imageName;
+ (UIBarButtonItem *)createLeftBarButtonItem:(NSString *)title target:(id)obj selector:(SEL)selector ImageName:(NSString*)imageName;

/**
 * 判断字符串中是否包含表情
 */
+ (BOOL)stringContainsEmoji:(NSString *)string;

/**
 * 获取当前时间
 */
+ (NSString *)getTime;

/**
 * string：  文本
 * font：    字体大小
 * size：    范围宽高
 */
+ (CGSize )getTextSizeWithString:(NSString *)string font:(UIFont *)font size:(CGSize )CustonSize;

@end
