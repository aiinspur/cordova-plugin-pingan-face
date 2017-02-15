//
//  PAZCLDefineTool.h
//  PAFaceCheck
//
//  Created by prliu on 16/3/22.
//  Copyright © 2016年 pingan. All rights reserved.
//

#ifndef PAZCLDefineTool_h
#define PAZCLDefineTool_h

#ifndef PAFacecheckController_ZCLDefineTool_h
#define PAFacecheckController_ZCLDefineTool_h
#define KFINISHMOUTHCHECK @"MouthCheck_finish"//张嘴
#define KFINISHHEADCHECK @"HeadShake_finish"//摇头
#define KFINISHFACECHECK @"FaceCheck_finish"//检脸
#define KFINISHAPICHECK @"api_finish"//应用结束

//活体检测时间 （默认为10）
#define kActionTime 15

#define kFaceImage(name)   [UIImage imageNamed:[NSString stringWithFormat:@"myface.bundle/%@", name]]
#define kFaceFilePath(path,type)   [[NSBundle mainBundle] pathForResource:[NSString stringWithFormat:@"myface.bundle/%@", path] ofType:type]


// 随机色
#define DRandomColor [UIColor colorWithRed:arc4random_uniform(255)/255.0 green:arc4random_uniform(255)/255.0 blue:arc4random_uniform(255)/255.0 alpha:arc4random_uniform(255)/255.0]

#define kTextColor [UIColor colorWithRed:93/255. green:88/255. blue:88/255. alpha:1.]

#define BACK_ACTION(block) dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), block)
#define MAIN_ACTION(block) dispatch_async(dispatch_get_main_queue(),block)

/* --------------------------------------------------------------------------------- */
#pragma mark - 屏幕适配

// 480  568  667  736
// 是否是4英寸
#define kIphone4 (480.0 == [[UIScreen mainScreen] bounds].size.height)
#define kIphone5 (568.0 == [UIScreen mainScreen].bounds.size.height)
#define kIphone6 (667.0 == [UIScreen mainScreen].bounds.size.height)
#define kIphone6p (736.0 == [UIScreen mainScreen].bounds.size.height)

#define kIOSVersions [[[UIDevice currentDevice] systemVersion] floatValue] //获得iOS版本
#define kUIWindow    [[[UIApplication sharedApplication] delegate] window] //获得window

#define kUnderStatusBarStartY (kIOSVersions>=7.0 ? 20 : 0)                 //7.0以上stautsbar不占位置，内容视图的起始位置要往下20

#define kScreenSize           [[UIScreen mainScreen] bounds].size                 //(e.g. 320,480)
#define kScreenWidth          [[UIScreen mainScreen] bounds].size.width           //(e.g. 320)
#define kScreenHeight         [[UIScreen mainScreen] bounds].size.height          //(e.g. 480)
#define kScreenHeightIOS7  (kIOSVersions>=7.0 ? [[UIScreen mainScreen] bounds].size.height + 64 : [[UIScreen mainScreen] bounds].size.height)

#define kScaleWidth(width)      ((width)*kScreenWidth)/320     //宽度按比例适配
#define kScaleHeight(height)   (kIphone4?(height):((height)*kScreenHeight)/568)    //高度按比例适配

#define kIOS7OffHeight (kIOSVersions>=7.0 ? 64 : 0)

#define kApplicationSize      [[UIScreen mainScreen] applicationFrame].size       //(e.g. 320,460)
#define kApplicationWidth     [[UIScreen mainScreen] applicationFrame].size.width //(e.g. 320)
#define kApplicationHeight    [[UIScreen mainScreen] applicationFrame].size.height//不包含状态bar的高度(e.g. 460)

#define kStatusBarHeight         20
#define kNavigationBarHeight     44
#define kNavigationheightForIOS7 64
#define kContentHeight           (kApplicationHeight - kNavigationBarHeight)
#define kTabBarHeight            49
#define kSeekTabBarHeight (kIOSVersions>=7.0 ? 0 : 49)

#pragma mark - 字体大小
/* --------------------------------------------------------------------------------- */
#define kStatementFontSize      [UIFont systemFontOfSize:12]       //陈述字体大小
#define kNavigationItemFontSize [UIFont systemFontOfSize:15]       //NavigationItem字体大小
#define kTextFontSize           [UIFont systemFontOfSize:16]       //正文字体大小
#define kButtonFontSize         [UIFont systemFontOfSize:19]       //按钮字体大小
#define kTitleFontSize          [UIFont systemFontOfSize:20]       //NavigationBar标题字体


//屏幕宽度 （区别于viewcontroller.view.fream）
#define WIN_WIDTH  [UIScreen mainScreen].bounds.size.width
//屏幕高度 （区别于viewcontroller.view.fream）
#define WIN_HEIGHT [UIScreen mainScreen].bounds.size.height

//输出测试使用
#ifdef DEBUG
#   define YTLog(XXX) NSLog(@"%@: %@", NSStringFromClass([self class]), XXX)
#else
#   define YTLog()
#endif

#if __LP64__ || (TARGET_OS_EMBEDDED && !TARGET_OS_IPHONE) || TARGET_OS_WIN32 || NS_BUILD_32_LIKE_64
#   define YTIntLog(XXX) NSLog(@"%@: %ld", NSStringFromClass([self class]), XXX)
#else
#   define YTIntLog(XXX) NSLog(@"%@: %d", NSStringFromClass([self class]), XXX)
#endif



//IOS版本
#define IOS_SysVersion [[UIDevice currentDevice] systemVersion].floatValue
#define KDocumentFile NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0]

#define NavigationBarHight 64

//----------------------颜色类---------------------------
// rgb颜色转换（16进制->10进制）
#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]
// color
#define YT_ColorWithRGB(R, G, B, A) [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:A]

//G－C－D
#define BACK_ACTION(block) dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), block)
#define MAIN_ACTION(block) dispatch_async(dispatch_get_main_queue(),block)

//NSUserDefaults 实例化
#define KUSER_DEFAULT [NSUserDefaults standardUserDefaults]

//block 宏
typedef void(^VoidBlock)();
typedef BOOL(^BoolBlock)();
typedef int (^IntBlock) ();
typedef id  (^IDBlock)  ();

typedef void(^VoidBlock_int)(NSUInteger);
typedef BOOL(^BoolBlock_int)(int);
typedef int (^IntBlock_int) (int);
typedef id  (^IDBlock_int)  (int);

typedef void(^VoidBlock_string)(NSString*);
typedef BOOL(^BoolBlock_string)(NSString*);
typedef int (^IntBlock_string) (NSString*);
typedef id  (^IDBlock_string)  (NSString*);

typedef void(^VoidBlock_id)(id);
typedef BOOL(^BoolBlock_id)(id);
typedef int (^IntBlock_id) (id);
typedef id  (^IDBlock_id)  (id);

typedef void(^VoidBlock_bool)(BOOL);

#define KSIG            @"megvii_liveness_demo"
#define KAPIKEY         @"api_key"
#define KAPISECRET      @"api_secret"
#define KSERVER         @"server_url"
#define KSHOWIMAGE      @"need_show_images"
#define KDEBUG          @"need_debug"

#define KDEFAULTHOSTAPI     @"http://test.faceid.com"
#define KDEFAULTAPIKEY      @"test"
#define KDEFAULTAPISECRET   @"test"

#endif


#endif /* PAZCLDefineTool_h */
