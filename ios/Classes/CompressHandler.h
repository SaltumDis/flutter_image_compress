//
// Created by cjl on 2018/9/8.
//

#import <Foundation/Foundation.h>


@interface CompressHandler : NSObject
+ (NSData *)compressWithData:(NSData *)data width:(int)width height:(int)height quality:(int)quality
                      rotate:(int)rotate format:(int)format;

+ (NSData *)compressWithUIImage:(UIImage *)image width:(int)width height:(int)height quality:(int)quality
                         rotate:(int)rotate format:(int)format;

+ (NSData *)compressDataWithUIImage:(UIImage *)image width:(int)width height:(int)height
                            quality:(int)quality rotate:(int)rotate format:(int)format;
@end
