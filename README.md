# XHSCapaScale(小红书发现页图片手势实现)
水文 2333
### 小红书发现页图片效果

以下由于gif制作太麻烦以下，小红书的发现页图片手势效果口述（当然你去下载看看也是可以的 ~~）
<br/>
当我们双指放在图片，图片可以全屏放大，移动（期间手势未中断，这里也就排除了dialog之类的实现，当然非要用dialog，应该也是可以实现的）。。。

### 怎么实现
#### 我第一个想到的是，双指放在图片上后，创建图片全屏显示层（后称为浮层），由外层的view添加该浮层,且这个浮沉支持所有的手势操作

要这样实现的话，存在以下几个注意点
<br/>
1.recyclerView 的item里图片实现双指监听，触发后创建浮层；
<br/>
2.浮层的手势传递，需要主动把触摸事件传递给浮沉(想办法把事件传给dialog当然也行,涉及到两次window的事件传递)；
<br/>
3.等等~~

#### 还有就是recyclerView里的item直接支持放大位移；
要这样实现的话，存在以下几个注意点
<br/>
1.主要item的层级，父布局clipChildren、clipToPadding用起来
<br/>
2.等等~~
<br/>
想想这种实现都超级麻烦

### 我们再简单的看一下小红书的实现
```
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt" xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/aw1" android:layout_width="match_parent" android:layout_height="match_parent"
    android:layout_gravity="center">
  <com.xingin.widgets.XYImageView android:id="@+id/aw0" android:layout_width="match_parent"
      android:layout_height="match_parent" android:adjustViewBounds="false"
      android:background="@color/i6" app:progressBarAutoRotateInterval="10000"
      app:progressBarImageScaleType="5"/>
  <com.xingin.tags.library.sticker.widget.CapaScaleView android:id="@+id/avz"
      android:layout_width="match_parent" android:layout_height="wrap_content"
      android:layout_gravity="center" android:background="@android:color/transparent"/>
</FrameLayout>

```
以上是小红书图片显示的布局，第一看上去，像是用的第二种方式实现，手势控件和图片都有了，且在item里面，
仔细看CapaScaleView并没有包住图片控件
<br/>
 ？？ 这是几个意思！！
 
 ### 经过好长的时间后，尝试各种逆向骚操作后~~
 小红书 直接在最外层创建ImageView，CapaScaleView处理手势，然后根据处理结果，直接对imageview做放大位移的处理啥的
 
 ### 以下是实现的简单Demo [https://github.com/genius158/XHSCapaScale](https://github.com/genius158/XHSCapaScale)