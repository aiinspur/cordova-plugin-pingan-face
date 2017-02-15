//
//  PAFaceCheckHome.h
//  PAFaceCheck
//
//  Created by prliu on 16/2/18.
//  Copyright © 2016年 PingAN. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
typedef void (^PAFaceCheckBlock)(NSDictionary *content);

/*!
 *  功能设置类型
 */
typedef enum {
    PACheck_FaceRecognization,      //人脸识别
    PACheck_FaceAndSoundGroove      //人脸识别加声纹识别（未上线）
} PACheckType;

/*!
 *  配置环境
 */
typedef enum {
    PAEnvironment_Prd = 1,      //生产环境
    PAEnvironment_Stg = 2       //测试环境
} PACheckEnvironment;


/**
 *  全屏图片信息
 */
struct PAFaceImageInfo{
    
    /** 是否包含人脸 */
    bool has_face;
    /** 左右旋转角度 */
    float yaw;
    /** 上下旋转角度 */
    float pitch;
    /** 运动模糊程度 */
    float blurness_motion;
    /** 人脸位置 */
    CGRect face_rect;
    /** 亮度 */
    float brightness;
    
};



#pragma mark ------------ PAFaceCheck ------------
/*!
 *  活体检测代理方法
 */
@protocol PACheckDelegate <NSObject>
@optional
/*!
 *  每一个活体动作检测的代理回调方法（APP处理网络时，错误回调）
 *  @param faceReport   每一个活体动作检测失败数据字典
 *  @param error        检测失败报错
 */
- (void)getSinglePAcheckReport:(NSDictionary *)singleReport error:(NSError *)error andThePAFaceCheckdelegate:(id)delegate;
;

/*!
 *  活体检测的代理回调方法(APP处理网络则不需要理会)
 *  @param faceReport   检测成功数据字典
 *  其中key有:     imageID       上传照片的流水号
 *              imageInfoArr    活体检测详细数据类数组
 （5个，第1个为正脸，2到5为每一步对应活体数据）
 （数据类型 参看PAFaceInfo）
 *
 *  @param faceImage    人脸正面照（即上传到服务器的照片）
 *  @param error        检测失败报错
 */
- (void)getPAcheckReport:(NSDictionary *)faceReport Image:(UIImage *)faceImage error:(NSError *)error;

/*!
 *  APP在此回调处理网络
 *  @param picture   完整图片
 *  @param faceImage    人脸正面照（即上传到服务器的照片）
 *  @param imageInfo    人脸正面照信息（自主选择）
 *  @param completion   回调网络请求结果（字典类型）setCompletionBlock/setFailedBlock
 */
-(int)getPacheckreportWithImage:(UIImage *)picture andTheFaceImage:(UIImage*)faceImage andTheFaceImageInfo:(NSDictionary*)imageInfo andTheResultCallBlacek:(PAFaceCheckBlock)completion;
@end


/*!
 *  活体检测视图控制器
 */
@interface PAFaceCheckHome : UIViewController <UIAlertViewDelegate>

- (id)initWithPAcheckWithTheCountdown :(BOOL)countDown andTheAdvertising:(NSString*)advertising  number0fAction:(NSString*)num voiceSwitch:(BOOL)voiceSwitch delegate:(id <PACheckDelegate>)faceDelegate;
@end





#pragma mark ------------ PAFaceInfo ------------
/*!
 *  SDK返回宿主APP的图片类
 *  通过该类可获取对应的人脸图片及图片质量分值
 */
@interface PAFaceInfo : NSObject

/** 全屏图片*/
@property (readonly) UIImage* image;

/** 人脸图片*/
@property (readonly) UIImage* faceImage;

/** 全屏图片信息 具体参数见 PAFaceImageInfo */
@property (readonly) struct PAFaceImageInfo imageInfo;

@end
