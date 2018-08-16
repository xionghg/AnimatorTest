# AnimatorTest

七夕将至，来个应景的爱心万花筒。

使用简单，无侵入，提供一个 Activity 或者 FrameLayout 即可：

```java
    // 简单做法：
    Kaleidoscope.with(Activity activity).start();
    // or:
    Kaleidoscope.with(FrameLayout frameLayout).start();

    // 自定义做法：
    Kaleidoscope.with(Activity activity)
            .total(/*爱心数量，默认100*/)
            .duration(/*总持续时间，默认5000ms*/)
            .singleDuration(/*单个爱心动画时间，默认1200ms*/)
            .sizeRule(/*爱心大小Rule，默认52dp*/)
            .colorRule(Kaleidoscope.RandomColorRule() /*爱心颜色Rule，提供一个随机颜色Rule*/)
            .start();
```

效果图：

![](https://github.com/xionghg/AnimatorTest/blob/master/picture/Kaleidoscope.gif?raw=true)
