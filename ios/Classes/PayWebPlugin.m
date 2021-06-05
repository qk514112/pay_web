#import "PayWebPlugin.h"
#if __has_include(<pay_web/pay_web-Swift.h>)
#import <pay_web/pay_web-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "pay_web-Swift.h"
#endif

@implementation PayWebPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPayWebPlugin registerWithRegistrar:registrar];
}
@end
