//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import RxSwift

public class Executor {

    /// Execute the given logic after the given delay assuming the given maximum frame duration.
    /// This allows excluding the time elapsed due to breakpoint pauses.
    ///
    /// - note: The logic closure is not guaranteed to be performed exactly after the given delay.
    ///   It may be performed later if the actual frame duration exceeds the given maximum frame
    ///   duration.
    ///
    /// - parameter delay: The delay to perform the logic, excluding any potential elapsed time
    ///   due to breakpoint pauses.
    /// - parameter maxFrameDuration: The maximum duration a single frame should take. Default to
    ///   33ms.
    /// - parameter logic: The closure logic to perform.
    public static func execute(withDelay delay: TimeInterval, maxFrameDuration: TimeInterval = 0.033, logic: @escaping () -> ()) {
        let period = maxFrameDuration / 3.0
        var lastRunLoopTime = Date().timeIntervalSinceReferenceDate
        var properFrameTime = 0.0
        var didExecute = false
        _ = Observable<Int>
            .timer(0, period: period, scheduler: MainScheduler.instance)
            .takeWhile { _ in
                return !didExecute
            }
            .subscribe(onNext: { _ in
                let currentTime = Date().timeIntervalSinceReferenceDate
                let trueElapsedTime = currentTime - lastRunLoopTime
                lastRunLoopTime = currentTime

                // If we did drop frame, we under-count the frame duration, which is fine. It
                // just means the logic is performed slightly later.
                let boundedElapsedTime = min(trueElapsedTime, maxFrameDuration)
                properFrameTime += boundedElapsedTime
                if properFrameTime > delay {
                    didExecute = true

                    logic()
                }
            })
    }
}
