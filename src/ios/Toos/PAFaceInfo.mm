//
//  PAFaceInfo.m
//  PAFaceCheck
//
//  Created by ken on 15/5/11.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "PALivenessDetector.h"
#import "PAFaceCheckHome.h"

@interface PAFaceInfo ()
{
    UIImage *_image;
    UIImage *_faceImage;
    PAFaceImageInfo _imageInfo;
}

- (void)setWithInfo:(PALivenessDetectionFrame *)info;

@end




@implementation PAFaceInfo
- (void)setWithInfo:(PALivenessDetectionFrame *)info{
    
    _image = info.image;
    _faceImage = [info croppedImageOfFace];
    
    _imageInfo.has_face         = info.attr.has_face;
    _imageInfo.yaw              = info.attr.yaw;
    _imageInfo.pitch            = info.attr.pitch;
    _imageInfo.blurness_motion= info.attr.blurness_motion;
    _imageInfo.face_rect        = info.attr.face_rect;
    _imageInfo.brightness       = info.attr.brightness;

}
@end
