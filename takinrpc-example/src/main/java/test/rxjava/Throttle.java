package test.rxjava;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class Throttle {

    public static void main(String[] args) {
        try {
            Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    try {
                        //前8个数字产生的时间间隔为1秒，后一个间隔为3秒
                        for (int i = 1; i < 9; i++) {
                            subscriber.onNext(i);
                        }
                        subscriber.onNext(1);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.newThread()).throttleFirst(2200, TimeUnit.MILLISECONDS) //采样间隔时间为2200毫秒
                            .subscribe(new Subscriber<Integer>() {
                                @Override
                                public void onNext(Integer item) {
                                    System.out.println("Next: " + item);
                                }

                                @Override
                                public void onError(Throwable error) {
                                    System.err.println("Error: " + error.getMessage());
                                }

                                @Override
                                public void onCompleted() {
                                    System.out.println("Sequence complete.");
                                }
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
