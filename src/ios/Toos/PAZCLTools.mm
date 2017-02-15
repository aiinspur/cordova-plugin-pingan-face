//
//  PAZCLTools.m
//  PAFaceCheck
//
//  Created by prliu on 16/3/22.
//  Copyright © 2016年 pingan. All rights reserved.
//

#import "PAZCLTools.h"

@implementation PAZCLTools
//static ZCLTools *_tools = nil;
//
//+(ZCLTools *)share{
//    @synchronized(self)
//    {
//        if (_tools == nil) {
//            _tools = [[ZCLTools alloc] init];
//        }
//        return _tools;
//    }
//}

/**
 * 判断字符串中是否包含表情
 */
+(BOOL)stringContainsEmoji:(NSString *)string {
    __block BOOL returnValue = NO;
    [string enumerateSubstringsInRange:NSMakeRange(0, [string length]) options:NSStringEnumerationByComposedCharacterSequences usingBlock:
     ^(NSString *substring, NSRange substringRange, NSRange enclosingRange, BOOL *stop) {
         
         const unichar hs = [substring characterAtIndex:0];
         // surrogate pair
         if (0xd800 <= hs && hs <= 0xdbff) {
             if (substring.length > 1) {
                 const unichar ls = [substring characterAtIndex:1];
                 const int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                 if (0x1d000 <= uc && uc <= 0x1f77f) {
                     returnValue = YES;
                 }
             }
         } else if (substring.length > 1) {
             const unichar ls = [substring characterAtIndex:1];
             if (ls == 0x20e3) {
                 returnValue = YES;
             }
             
         } else {
             // non surrogate
             if (0x2100 <= hs && hs <= 0x27ff) {
                 returnValue = YES;
             } else if (0x2B05 <= hs && hs <= 0x2b07) {
                 returnValue = YES;
             } else if (0x2934 <= hs && hs <= 0x2935) {
                 returnValue = YES;
             } else if (0x3297 <= hs && hs <= 0x3299) {
                 returnValue = YES;
             } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50) {
                 returnValue = YES;
             }
         }
     }];
    
    return returnValue;
}

+ (UIBarButtonItem *)createRightBarButtonItem:(NSString *)title target:(id)obj selector:(SEL)selector ImageName:(NSString*)imageName
{
    UIImage *image = [UIImage imageNamed:imageName];
    UIButton *rightButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [rightButton setImage:image forState:UIControlStateNormal];
    [rightButton setTitle:title forState:UIControlStateNormal];
    //FIX ME:!!!不是所有的右上按钮都是更多！
    rightButton.accessibilityLabel = title;
    rightButton.titleLabel.font = [UIFont systemFontOfSize:17];
    [rightButton setTitleColor:[UIColor colorWithRed:55/255.0 green:203/255.0 blue:253/255.0 alpha:1.0f] forState:UIControlStateNormal];
    [rightButton setTitleColor:[UIColor lightGrayColor] forState:UIControlStateHighlighted];
    //    [rightButton setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [rightButton setTitleColor:[UIColor colorWithWhite:1 alpha:0.6] forState:UIControlStateDisabled];
    [rightButton addTarget:obj action:selector forControlEvents:UIControlEventTouchUpInside];
    [rightButton sizeToFit];
    [rightButton setAccessibilityLabel:title];
    //iOS7之前的版本需要手动设置和屏幕边缘的间距
    if (kIOSVersions < 7.0) {
        rightButton.frame = CGRectInset(rightButton.frame, -10, 0);
    }
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:rightButton];
    return item;
}

+ (UIBarButtonItem *)createLeftBarButtonItem:(NSString *)title target:(id)obj selector:(SEL)selector ImageName:(NSString*)imageName
{
    if (!imageName) {
        imageName = @"hd_back";
    }
    UIImage *image = [UIImage imageNamed:imageName];
    UIButton *leftButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [leftButton setImage:image forState:UIControlStateNormal];
    [leftButton setAccessibilityLabel:title];
    [leftButton setAccessibilityLabel:@"返回"];
    
    //    CGSize titleSize = [title sizeWithAttributes:[UIFont systemFontOfSize:18]];
    //    if (titleSize.width < 44) {
    //        titleSize.width = 44;
    //    }
    //    leftButton.frame = CGRectMake(0, 0, titleSize.width, 44);
    leftButton.titleLabel.font = [UIFont systemFontOfSize:18];
    leftButton.titleLabel.textAlignment = NSTextAlignmentLeft;
    leftButton.titleLabel.adjustsFontSizeToFitWidth = YES;
    [leftButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [leftButton setTitleColor:[UIColor lightGrayColor] forState:UIControlStateHighlighted];
    //    [leftButton setTitleColor:[UIColor colorWithRed:130.0/255 green:56.0/255 blue:23.0/255 alpha:1] forState:UIControlStateHighlighted];
    [leftButton setTitleColor:[UIColor colorWithWhite:1 alpha:0.6] forState:UIControlStateDisabled];
    [leftButton addTarget:obj action:selector forControlEvents:UIControlEventTouchUpInside];
    [leftButton sizeToFit];
    
    //iOS7之前的版本需要手动设置和屏幕边缘的间距
    if (kIOSVersions < 7.0) {
        leftButton.imageEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 0);
    }else{
        leftButton.imageEdgeInsets = UIEdgeInsetsMake(0, -10, 0, 0);
        leftButton.titleEdgeInsets = UIEdgeInsetsMake(0, -10, 0, 0);
    }
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:leftButton];
    return item;
}

/**
 * 获取当前时间
 */
+ (NSString *)getTime{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat: @"yyyyMMddHHmmss"];
    [formatter setTimeZone:[NSTimeZone timeZoneWithName:@"GMT"]];
    
    NSString *timeDesc = [formatter stringFromDate:[NSDate date]];
    return timeDesc;
}

/**
 * string：  文本
 * font：    字体大小
 * size：    范围宽高
 */
+ (CGSize )getTextSizeWithString:(NSString *)string font:(UIFont *)font size:(CGSize )CustonSize
{
    NSDictionary *attribute = @{NSFontAttributeName: font};
    CGSize textSize = [string boundingRectWithSize:CustonSize options: NSStringDrawingTruncatesLastVisibleLine |NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:attribute context:nil].size;
    return textSize;
}


@end
