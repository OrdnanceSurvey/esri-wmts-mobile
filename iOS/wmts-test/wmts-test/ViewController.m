//
//  ViewController.m
//  wmts-test
//
//  Created by David Haynes on 29/01/2016.
//  Copyright © 2016 Ordnance Survey. All rights reserved.
//

#import <ArcGIS/ArcGIS.h>
#import "ViewController.h"

@interface ViewController ()<AGSMapViewLayerDelegate, AGSWMTSInfoDelegate>

@property (weak, nonatomic) IBOutlet AGSMapView *mapView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    self.mapView.layerDelegate = self;

    NSString *basePath = @"https://api2.ordnancesurvey.co.uk/mapping_api/v1/service/wmts";
    NSString *apiKey = self.apiKey;
    NSString *wmtsPath = [NSString stringWithFormat:@"%@?key=%@", basePath, apiKey];
    NSURL *wmtsURL = [NSURL URLWithString:wmtsPath];
    AGSWMTSInfo *wmtsInfo = [[AGSWMTSInfo alloc] initWithURL:wmtsURL];
    wmtsInfo.delegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (NSString *)apiKey {
    return [[NSString stringWithContentsOfURL:[NSBundle.mainBundle URLForResource:@"APIKEY" withExtension:nil]
                                     encoding:NSUTF8StringEncoding
                                        error:nil] stringByReplacingOccurrencesOfString:@"\n"
                                                                             withString:@""];
}

#pragma mark - AGSMapViewLayerDelegate

- (void)mapViewDidLoad:(AGSMapView *)mapView {
    [self.mapView.locationDisplay startDataSource];
    self.mapView.locationDisplay.autoPanMode =
        AGSLocationDisplayAutoPanModeDefault;
}

#pragma mark - AGSWMTSInfoDelegate

- (void)wmtsInfoDidLoad:(AGSWMTSInfo *)wmtsInfo {
    NSLog(@"%@", wmtsInfo);

    NSArray *layerInfos = [wmtsInfo layerInfos];
    AGSWMTSLayerInfo *layerInfo = [layerInfos objectAtIndex:0];
    AGSWMTSLayer *wmtsLayer = [wmtsInfo wmtsLayerWithLayerInfo:layerInfo andSpatialReference:nil];
    [self.mapView addMapLayer:wmtsLayer withName:@"wmts layer"];
}

- (void)wmtsInfo:(AGSWMTSInfo *)wmtsInfo didFailToLoad:(NSError *)error {
    NSLog(@"WMTS Failed to load");
}

@end
