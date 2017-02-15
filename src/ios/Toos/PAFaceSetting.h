//
//  PAFaceSetting.h
//  PAFaceCheck
//
//  Created by ken on 15/5/14.
//  Copyright (c) 2015年 PingAN. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PAFaceCheckHome.h"

@interface PAFaceSetting : NSObject


+ (PAFaceSetting *)share;

-(void)setEnvironmentWithDict:(NSDictionary *)environmentDict;

- (void)setEnvironment:(PACheckEnvironment)environment
                  uuid:(NSString *)uuid
                appKey:(NSString *)appKey
              systemId:(NSString *)subSystemId;



@property (readonly) NSString *appKey;
@property (readonly) NSString *uuid;
@property (readonly) PACheckEnvironment environment;
@property (readonly) NSString *subSystemId;
@property (readonly) NSDictionary *environmentDict;
@property (readonly) NSString *url;


@end
