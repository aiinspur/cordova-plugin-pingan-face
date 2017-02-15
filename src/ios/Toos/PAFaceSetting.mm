//
//  PAFaceSetting.m
//  PAFaceCheck
//
//  Created by ken on 15/5/14.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import "PAFaceSetting.h"

#define kFaceStgUrl @"https://test1-opraeams.pingan.com.cn:49444/opra-eams-portal/face"
#define kFacePrdUrl @"https://opraeams.pingan.com.cn/opra-eams-portal/face"


@interface PAFaceSetting ()
{
    NSString *_uuid;
    NSString *_appKey;
    NSString *_subSystemId;
    PACheckEnvironment _environment;
    NSDictionary *_environmentDict;
    NSString *_type;
}

@end

@implementation PAFaceSetting

static PAFaceSetting *_setting = nil;

+(PAFaceSetting *)share{
    @synchronized(self)
    {
        if (_setting == nil) {
            _setting = [[PAFaceSetting alloc] init];
            
        }
        return _setting;
    }
}

-(void)setEnvironmentWithDict:(NSDictionary *)environmentDict{
    
    _environmentDict = [[NSDictionary alloc]init];
    _environmentDict = environmentDict;
    if ([environmentDict objectForKey:@"environment"] && [[environmentDict objectForKey:@"environment"] isEqualToString:@"PAEnvironment_Prd"]) {
        _environment = PAEnvironment_Prd;
    }else if ([environmentDict objectForKey:@"environment"] && [[environmentDict objectForKey:@"environment"] isEqualToString:@"PAEnvironment_Stg"]){
        _environment = PAEnvironment_Stg;
    }

}

- (void)setEnvironment:(PACheckEnvironment)environment
                  uuid:(NSString *)uuid
                appKey:(NSString *)appKey
              systemId:(NSString *)subSystemId
{
    _environment = environment;
    _uuid = uuid;
    _appKey = appKey;
    _subSystemId = subSystemId;
}

- (NSString *)url{
    
   return [_environmentDict objectForKey:@"url"];
    
}
@end
