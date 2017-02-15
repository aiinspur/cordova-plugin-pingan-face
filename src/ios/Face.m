/********* Face.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "PAFaceCheckHome.h"

@interface Face : CDVPlugin<PACheckDelegate> {
    // Member variables go here.
    CDVInvokedUrlCommand *m_command;
}

- (void)coolMethod:(CDVInvokedUrlCommand*)command;
@end

@implementation Face

- (void)coolMethod:(CDVInvokedUrlCommand*)command
{
//    CDVPluginResult* pluginResult = nil;
//    NSString* echo = [command.arguments objectAtIndex:0];
//
//    if (echo != nil && [echo length] > 0) {
//        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
//    } else {
//        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
//    }
//
//    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    m_command = command;
    
    PAFaceCheckHome *faceVC = [[PAFaceCheckHome alloc] initWithPAcheckWithTheCountdown:YES andTheAdvertising:@"" number0fAction:@"0" voiceSwitch:YES delegate:self];
    [self.viewController presentViewController:faceVC animated:YES completion:nil];
}

/*!
 *  每一个活体动作检测的代理回调方法
 *  @param singleReport   单步检测失败数据字典
 *  @param error        检测失败报错
 */
- (void)getSinglePAcheckReport:(NSDictionary *)singleReport error:(NSError *)error andThePAFaceCheckdelegate:(id)delegate
{
    
    if (error) {
        
        NSDictionary *errorInfo = [[NSDictionary alloc]initWithDictionary:singleReport];
        //宿主APP展示活体检测返回信息
        NSLog(@"检测到人脸未成功");
        
        switch ([[singleReport objectForKey:@"failedType"] intValue]) {
            case 1:
                
                NSLog(@"DETECTION_FAILED_TYPE_DiscontinuityAttack");
                break;
            case 2:
                NSLog(@"DETECTION_FAILED_TYPE_NOTVIDEO");
                
                break;
            case 3:
                NSLog(@"DETECTION_FAILED_TYPE_TIMEOUT");
                
                break;
            case 4:
                NSLog(@"DETECTION_FAILED_TYPE_Interrupt");
                break;
            case 5:
                NSLog(@"DETECTION_FAILED_TYPE_License");
                break;
            default:
                break;
        }
        
        NSLog(@"%@",errorInfo);
        
    }
}


#pragma mark -- 重写网络接口

//重写网络接口，默认不需要，如果想自己接手管理网络交互则重写，回调的前提是andTheInterfaceType 为”0”。
-(int)getPacheckreportWithImage:(UIImage *)picture andTheFaceImage:(UIImage *)faceImage andTheFaceImageInfo:(NSDictionary *)imageInfo andTheResultCallBlacek:(PAFaceCheckBlock)completion{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        
        //如下代码为必需
        NSMutableDictionary *resuDict = [[NSMutableDictionary alloc] initWithDictionary:imageInfo];
        [resuDict setValue:@"易认证回调" forKey:@"成功检测到人脸"];
        if (completion) {
            completion (resuDict);
        }
        
        
        NSLog(@"faceImage = %@",faceImage);
        NSLog(@"text = %@",[imageInfo objectForKey:@"infoText"]);
        
        if (faceImage) {
            //图片路径
            //jpg格式
            //            NSData *imageData=UIImagePNGRepresentation(faceImage);
            NSData *imageData = UIImageJPEGRepresentation(faceImage,1.0);
            NSArray*paths=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
            NSString *documentsDirectory=[paths objectAtIndex:0];
            
            NSString *timeStamp = [self getTime];
            NSString *fileNameStr = [NSString stringWithFormat:@"%@.jpg",timeStamp];

            NSString *savedImagePath=[documentsDirectory stringByAppendingPathComponent:fileNameStr];
            [imageData writeToFile:savedImagePath atomically:YES];
            
            NSString *imagePath = savedImagePath;
            
            //返回结果
            //@"rotate" @"yaw" @"motion_blurness" @"head_motion" @"mouth_motion" @"pitch"
            //@"brightness" @"eye_hwratio" @"rect_x" @"rect_y" @"rect_w" @"rect_h"
            
            NSMutableDictionary *tempDic = [[NSMutableDictionary alloc] init];
            NSMutableDictionary *face_rect_dic = [imageInfo objectForKey:@"face_rect"];
            [tempDic setValue:[face_rect_dic objectForKey:@"face_rect_top_left"] forKey:@"rect_x"];
            [tempDic setValue:[face_rect_dic objectForKey:@"face_rect_top_right"] forKey:@"rect_y"];
            [tempDic setValue:[face_rect_dic objectForKey:@"face_rect_width"] forKey:@"rect_w"];
            [tempDic setValue:[face_rect_dic objectForKey:@"face_rect_height"] forKey:@"rect_h"];
            [tempDic setValue:imagePath forKey:@"imgurl"];
            [tempDic setValue:[imageInfo objectForKey:@"brightness"] forKey:@"brightness"];
            [tempDic setValue:[imageInfo objectForKey:@"blurness_motion"] forKey:@"motion_blurness"];
            [tempDic setValue:[imageInfo objectForKey:@"deflection_v"] forKey:@"deflection_v"];
            [tempDic setValue:[imageInfo objectForKey:@"content_type"] forKey:@"content_type"];
            
            [tempDic setValue:@"" forKey:@"rotate"];
            [tempDic setValue:@"" forKey:@"yaw"];
            [tempDic setValue:@"" forKey:@"head_motion"];
            [tempDic setValue:@"" forKey:@"mouth_motion"];
            [tempDic setValue:@"" forKey:@"pitch"];
            [tempDic setValue:@"" forKey:@"eye_hwratio"];
            
            NSString *resultJsonStr = [self dictionaryToJson:tempDic];
            NSLog(@"resultJsonStr = \n%@",resultJsonStr);
            
            //插件回调
            CDVPluginResult* pluginResult = nil;
            
            if (resultJsonStr != nil && [resultJsonStr length] > 0) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:resultJsonStr];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            }
            
            [self.commandDelegate sendPluginResult:pluginResult callbackId:m_command.callbackId];
        }
        
    });
    
    return 0;
}

//字典转json
- (NSString*)dictionaryToJson:(NSDictionary *)dic
{
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&parseError];
    
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

- (NSString *)getTime{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat: @"yyyyMMddHHmmss"];
    [formatter setTimeZone:[NSTimeZone timeZoneWithName:@"GMT"]];
    
    NSString *timeDesc = [formatter stringFromDate:[NSDate date]];
    return timeDesc;
}

@end
