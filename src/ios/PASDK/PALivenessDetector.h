//
//  PALivenessDetector.h
//  PALivenessDetector
//
//  Created by 刘沛荣 on 15/11/3.
//  Copyright © 2015年 PA. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

/**
 Environmental ErrorEnum
 */
typedef enum EnvironmentalErrorEnum {
    
    ENVIORNMENT_ERROR_TOOCLSE = 0,              ///<过近<请稍微退后>
    ENVIORNMENT_ERROR_TOOFAR = 1,               ///<过远<请稍微靠近>
    ENVIORNMENT_ERROR_TOOBRIGHT = 2,            ///<过于明亮
    ENVIORNMENT_ERROR_TOODARK = 3,              ///<过于灰暗
    ENVIORNMENT_ERROR_TOOPARTIAL = 4,           ///<过于偏转<请稍微正对摄像头>
    ENVIORNMENT_ERROR_NOFACE = 5,               ///<没有人脸<请稍微正对摄像头>
    ENVIORNMENT_ERROR_FUZZY = 6,               ///<图片模糊值过高
    ENVIORNMENT_ERROR_MOVEMENT = 7,            ///<请保持相对静止
    ENVIORNMENT_ERROR_MULTIFACE = 8,           ///<多人存在
    ENVIORNMENT_ERROR_NORMAL = 9              ///<正常
    
} EnvironmentalErrorEnum;


/*!
 *  \enum PALivenessDetectionType
 *  \brief 检测类型选项
 */
typedef enum PALivenessDetectionType {
    DETECTION_TYPE_NONE = 0,                ///<初始状态
    DETECTION_TYPE_MOUTH = 1,               ///<张嘴
    DETECTION_TYPE_POS_YAW = 2             ///<左右转头
    
} PALivenessDetectionType;


/*!
 *  \enum PALivenessDetectionFailedType
 *  \brief 检测失败类型
 */

typedef enum PALivenessDetectionFailedType {
    DETECTION_FAILED_TYPE_ACTIONBLEND = 0,          ///<动作错误
    DETECTION_FAILED_TYPE_DiscontinuityAttack = 1,  ///非连续性攻击
    DETECTION_FAILED_TYPE_NOTVIDEO = 2,             ///<不是活体
    DETECTION_FAILED_TYPE_TIMEOUT = 3,               ///<超时
    DETECTION_FAILED_TYPE_Interrupt = 4,          ///<用户操作
    DETECTION_FAILED_TYPE_License = 5             ///<摄像头不允许操作
} PALivenessDetectionFailedType;


/*!
 *
 */
typedef enum PALivenessFrameType {
    FRAME_TYPE_EyeDetection = 0,                            ///<眼睛检测
    FRAME_TYPE_FaceDetection = 1                            ///<人脸检测
} PALivenessFrameType;

/** @defgroup DetectorOptionKeys 检测器配置选项
 *  用于配置检测器的选项列表
 *  @{
 */

/** 模型路径 */
extern NSString* const PALivenessDetectorModelPath;
/** 模型内容 */
extern NSString* const PAlivenessDetectorModelRawData;
/** 通信密钥 */
extern NSString* const PALivenessDetectorCipher;
/** 每个检测环节的超时时限，默认10 */
extern NSString* const PALivenessDetectorStepTimeLimit;
/** 质量判断－最小人脸大小 [100, 8192], 默认150 */
extern NSString* const PALivenessDetectorMinFaceSize;
/** 质量判断－最大水平角度偏差 [0.0, 1.0]，默认0.17 */
extern NSString* const PALivenessDetectorMaxYawAngle;
/** 质量判断－最大上下角度偏差 [0.0, 1.0]，默认0.17 */
extern NSString* const PALivenessDetectorMaxPitchAngle;
/** 质量判断－人脸亮度最小平均值 [0, 255]，默认80 */
extern NSString* const PALivenessDetectorMinBrightness;
/** 质量判断－人脸亮度最大平均值 [0, 255]，默认170 */
extern NSString* const PALivenessDetectorMaxBrightness;
/** 质量判断－运动模糊阈值 [0.0, 1.0]，默认0.1 */
extern NSString* const PALivenessDetectorMaxMotionBlurness;
/** 质量判断－高斯模糊阈值 [0.0, 1.0]，默认0.08 */
extern NSString* const PALivenessDetectorMaxGaussianBlurness;
/** 质量判断－眼睛睁闭阈值 [0.0, 1.0]，默认0.3 */
extern NSString* const PALivenessDetectorMaxEyeOpen;
/** 质量判断－嘴部开闭阈值 [0.0, 1.0]，默认0.4 */
extern NSString* const PALivenessDetectorMaxMouthOpen;
/** 质量判断－有效面积比 */
extern NSString* const PALivenessDetectorMinIntegrity;
/** 动作个数－活体过程中的动作个数 */
extern NSString *PALivenessDetectorNumberOfAction;


/**
 *  人脸识别参数
 */
struct PAFaceAttr{
    
    /** 是否包含人脸 */
    bool has_face = false;
    /** 左右旋转角度 */
    float yaw = 0;
    /** 上下旋转角度 */
    float pitch = 0;
    /** 运动模糊程度 */
    float blurness_motion = 0;
    /** 人脸位置 */
    CGRect face_rect;
    /** 眼睛睁闭 */
    boolean_t eye_hwratio = 0;
    /** 亮度 */
    float brightness = 0;
    
};

/*!
 *  单帧检测结果的类，包含单帧检测出人脸的所有属性，此类无需构造，仅用于回调
 */
@interface PALivenessDetectionFrame : NSObject

/** 检测帧对应图片*/
@property (readonly) UIImage* image;

/** 图片中的人脸属性 */
@property (readonly) PAFaceAttr attr;
/** 根据人脸位置裁剪仅包含人脸的图片 */
-(UIImage*) croppedImageOfFace;
-(PALivenessDetectionFrame*) croppedFrameOfFaceWithMaxImageSize: (int) maxImageSize;

@end

/*!
 *
 */
@interface PAFaceIDData : NSObject

@property (readonly) NSDictionary* images;
@property (readonly) NSString* delta;

@end

/*!
 *  此接口通过 PALivenessDetector 的 setDelegate 函数进行注册，在活体检测的过程中会经由不同的事件触发不同的回调方法
 */
@protocol PALivenessProtocolDelegate <NSObject>

@required

/**
 *  @brief  倒计时
 *
 *  @return 剩余的时间
 */
-(void)theCounterDownWithCurrent:(int)currentTime;
/*!
 *  当前活体检测的动作检测失败时，调用此方法。
 *
 *  @param failedType 动作检测失败的原因（具体选项请参照PALivenessDetectionFailedType）
 *  @return 无
 */
-(void)onDetectionFailed:(PALivenessDetectionFailedType)failedType;
/*!
 *  当前活体检测更换动作时调用
 *
 *  @param PALivenessFrameType 动作状态（具体选项请参照PALivenessFrameType）
 *  @return 无
 */
-(void)onDetectionChangeAnimation:(PALivenessDetectionType)frameType;

/**
 *  @brief  检测过程中对于用户行为的建议
 *
 *  @param testType 用户行为错误类型
 */
-(void)onFrameDetectedForTheUserBehaviorInTheProcessOfTesting:(EnvironmentalErrorEnum)testType;

/*!
 *  当前活体检测的动作检测成功时，调用此方法。
 *  @param validFrame 当前动作中采集的质量最好帧。
 *  @return 下一个需要进行检测的动作的枚举类型（具体选项请参照PALivenessDetectionType），如果需要结束活体检测流程，则返回类型DETECTION_TYPE_DONE
 */
- (void)onDetectionSuccess:(PALivenessDetectionFrame *)faceInfo;

@end


@interface PALivenessDetector : NSObject
/**回调接口**/
@property(nonatomic,assign)id<PALivenessProtocolDelegate>delegate;
/*!
 *  检测器的构造函数，对于所有检测请求默认使用 DISPATCH_QUEUE_PRIORITY_DEFAULT 队列进行异步调用
 *
 *  @param options 包含构造检测器的各种配置，具体选项请参照 @link DetectorOptionKeys "检测器配置选项"@endlink
 *  @return 根据配置生成的新检测器，若初始化失败，则返回nil
 */
+(PALivenessDetector*) detectorOfOptions: (NSDictionary*) options;

/*!
 *  将视频流获取的原始数据传入检测器进行异步活体检测，检测结果将以异步的形式通知delegate
 *
 *  @param imgBuffer AVCaptureOutput获取的原始数据流，RGB格式
 *  @param orientation 手机朝向，用于旋转/翻转图像
 *  @return 请求是否成功
 */
-(bool) detectWithBuffer: (CMSampleBufferRef) imgBuffer orientation:(UIImageOrientation) orientation;


/*!
 *  重置Detector类的状态，当用户需要重新开始活体检测流程时，调用此函数。
 *
 *  @return 无
 */
-(void) reset;

@end
