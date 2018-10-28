package com.test;

import android.graphics.Bitmap;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hugh on 2018/2/12.
 */

public class RxJavaTest {
    public void testRxFolder(List<File> folders) {

        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                //e.onComplete();
                e.onError(new NullPointerException());
            }
        });

        //subscribe方法返回void类型
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        observable.subscribeOn(Schedulers.io());
        observable.observeOn(AndroidSchedulers.mainThread());
        compositeDisposable.dispose();


        Observable<File> observable1 = Observable.fromArray(folders.toArray());

        observable1.filter(new Predicate<List<File>>() {
                    @Override
                    public boolean test(List<File> fileList) throws Exception {
                        return file.getName().endsWith(".png");
                    }
                }).flatMap(new Function<List<File>, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(List<File> file) throws Exception {
                        Observable.fromIterable(Arrays.asList(file.listFiles()));
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private void simpleFun(){
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
                //e.onError(new NullPointerException());
            }
        });
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });


        Disposable disposable = observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //这里接收数据项
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                //这里接收onError
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                //这里接收onComplete。
            }
        });


        Disposable disposable1 = observable.subscribe(
                new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //这里接收数据项
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //这里接收onError
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //这里接收onComplete。
                    }
                });
    }


    private void  flowTest() {
        Flowable<String> stringFlowable = Flowable
                .create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> e) throws Exception {
                        e.onNext("1");
                        e.onNext("2");
                        e.onComplete();
                        //e.onError(new NullPointerException());
                    }
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());

        stringFlowable.subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                //s.request(1);
                // 参数表示接受多少次onNext回调.
                // 当onNext回调次数和这个参数不一致时, 则通过BackpressureStrategy.ERROR参数决定之后的处理
                // 不调用等价于request(0).
                // 典型的错误MissingBackpressureException异常
                // 注意: Flowable请尽量在异步线程使用,否则很容易出现MissingBackpressureException异常
            }

            @Override
            public void onNext(String s) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });

    }

    private Bitmap getBitmapFromFile(File file) {
        return null;
    }

}
