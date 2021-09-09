# hlj-dialog

* 使用方式

```
allprojects {
    repositories {
        ...
        maven { url 'http://172.16.12.102:8081/artifactory/libs-release-local/' }
    }
}
```

```
implementation 'com.hunliji:hlj-dialog:1.0.18'
```

混淆
```
-keep class **.model.**{*;}
```

依赖

```
implementation 'com.hunliji:integration-mw:1.0.1'
```

* 婚礼纪通用样式dialog，可包含题目，第一内容，第二内容，单选，多选

```
Context.showDialogSample(
    content: String
){
    confirm{dialog}
    cancel{dialog}
    dismiss{dialog}
    setTitle(title: String)
}
```

方法|作用
---- | ----
setTitle(title: String) | 设置标题，不设置则不显示标题
titleColor(titleColorRes: Int/titleColor:String) | 标题颜色，默认#000000
contentColor(contentColorRes: Int/contentColor:String) | 内容颜色 默认#333333
setSecondLine(secondLine: String) | 第二内容，不设置不显示
secondLineColor(secondLineColorRes: Int/secondLineColor:String) | 第二内容颜色 默认#333333
confirmText(confirmText: String) | 右侧按钮内容，默认确定
confirmColor(confirmColor: Int) | 右侧按钮颜色，默认R.color.colorPrimary
confirm{dialog} | 确认按钮点击
cancelText(cancelText: String) | 左侧按钮内容，默认取消
cancelColor(cancelColor: Int) |  左侧按钮颜色，默认#999999
cancel{dialog} | 取消按钮点击
corner(corner: Int) | 圆角 默认15
isSingle(isSingle: Boolean) | 是否去掉取消按钮，默认不去掉
width(width: Int) | 宽度 默认300.dp
height(height: Int) | 高度 默认自适应
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
autoDismiss(autoDismiss:Boolean) | 确认或者取消时是否自动关闭，默认自动关闭
canCancelOutSide(cancelOutSide: Boolean) | 点击阴影区域是否可关闭弹窗，默认可以，如果设置为false,此时点击back键仍然是可以关闭的
cancelAble(cancel: Boolean) | 是否可关闭弹窗，默认可以，如果设置为false,此时点击back键则不可以关闭
dismiss{dialog} | 弹窗消失回调
extra{dialog} | 一些对dialog额外的操作

* 简单的自定义dialog,通用样式满足不了，但只有确定/取消，没有复杂的业务逻辑

```
Context.showDialogSampleCustomer(
    layoutId: Int,
    init: SampleCustomerDialogBuilder.() -> Unit
)
```

方法|作用
---- | ----
convert{dialog} | 设置一些简单的属性，通过dialog.findViewById()
dialogType(DialogType) | 默认center,可选bottom (如果具有软键盘，则不要使用bottom,一般如果是底部弹出，更推荐使用Bottom或者part)
dialogAnimType(Int) | 只有dialogType为center的时候才有效果,默认是center,可选bottom
confirmId(id: Int) | 所提供的布局中确定按钮的id
cancelId(id: Int) | 所提供的布局中取消按钮的id
confirm{dialog} | 确认按钮点击
cancel{dialog} | 取消按钮点击
width(width: Int) | 宽度 默认300.dp
height(height: Int) | 高度 默认自适应
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
autoDismiss(autoDismiss:Boolean) | 确认或者取消时是否自动关闭，默认自动关闭
canCancelOutSide(cancelOutSide: Boolean) | 点击阴影区域是否可关闭弹窗，默认可以，如果设置为false,此时点击back键仍然是可以关闭的
cancelAble(cancel: Boolean) | 是否可关闭弹窗，默认可以，如果设置为false,此时点击back键则不可以关闭
dismiss{dialog} | 弹窗消失回调

* 自定义dialog,可处理一定复杂的业务逻辑

```
fun <T> Context.showDialogCustomer(
    dialogInfo: DialogCustomerInterface<T>
) {
}
```

方法|作用
---- | ----
dialogType(DialogType) | 默认center,可选bottom (如果具有软键盘，则不要使用bottom,一般如果是底部弹出，更推荐使用Bottom或者part)
dialogAnimType(Int) | 只有dialogType为center的时候才有效果,默认是center,可选bottom
width(width: Int) | 宽度 默认300.dp
height(height: Int) | 高度 默认自适应
confirm{dialog,T?} | 确认按钮点击 (这里和上面两个不一样，注意一下)
cancel{dialog,T?} | 取消按钮点击 (这里和上面两个不一样，注意一下)
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
autoDismiss(autoDismiss:Boolean) | 确认或者取消时是否自动关闭，默认自动关闭
canCancelOutSide(cancelOutSide: Boolean) | 点击阴影区域是否可关闭弹窗，默认可以，如果设置为false,此时点击back键仍然是可以关闭的
cancelAble(cancel: Boolean) | 是否可关闭弹窗，默认可以，如果设置为false,此时点击back键则不可以关闭
dismiss{dialog,T?} | 弹窗消失回调 (这里和上面两个不一样，注意一下)
requestMix(block: suspend CoroutineScope.() -> T,build: DialogRequestBuilder<T>.() -> Unit = {}) | 支持网络请求
showLoading() | 显示loading
hideLoading() | 隐藏loading

```
实现DialogCustomerInterface,其中T代表确定，取消，消失时所获取的返回值，如果没有，则随便给一个泛型
没有的话这里返回null
override fun onConfirmResult(): String? {
   return currentText
}
```

* 自底部划出的dialog,可拖拽

```
Context.showDialogBottom(
    popUp: BottomPopupView
){
}
```

方法|作用
---- | ----
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
show{} | 显示时回调
dismiss{} | 消失时回调

```
使用时需要提供BottomPopupView,继承来实现即可
```

* 位于底部，并且有输入框时，则当输入框弹出时，底部会依靠于输入框向上移动，可拖拽

```
Context.showDialogEditableBottomAlain(
    popUp: BottomPopupView
){
}
```

方法|作用
---- | ----
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
show{} | 显示时回调
dismiss{} | 消失时回调

```
使用时需要提供BottomPopupView,继承来实现即可
```

* 位于中间，并且有输入框时，则当输入框弹出时，底部会依靠于输入框

```
Context.showDialogEditableCenterAlain(
    popUp: CenterPopupView
){
}
```

方法|作用
---- | ----
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
show{} | 显示时回调
dismiss{} | 消失时回调
BasePopupView.showToggle(vararg views: BasePopupView) | 用于同一页面多个dialog互斥显示

```
使用时需要提供CenterPopupView,继承来实现即可
```

* 自动定位的弹窗，给定锚点view,如果位于屏幕下半部分，则向上划出，如果位于屏幕上半部分，则向下划出，推荐使用锚点为屏幕上半部分

```
Context.createDialogPartShadow(
    popUp: PartShadowPopupView,
    v: View
):Dialog{

}
```

方法|作用
---- | ----
showBehind(showBehind: Boolean) | 是否显示背景阴影，默认显示
show{} | 显示时回调
dismiss{} | 消失时回调

```
使用时需要提供PartShadowPopupView,继承来实现即可
```

* 注意：

```
在以上涉及到了许多与softInput相关，这里描述一下在不同场景下softInput的表现以及控制，推荐通过ext-mw库来设置

(1) 如果在activity/fragment中希望打开页面时显示弹窗，则调用
edit.showSoftActivityPan() 底部不向上顶
edit.showSoftActivityResize() 底部向上顶

(2) 如果使用
fun <T> Context.showDialogCustomer(
            dialogInfo: DialogCustomerInterface<T>
        ) {
}
则如果有softInput时，
显示：
edit.showSoftDialogPan()
隐藏：
edit?.context?.findActivity()?.forceHideSoftInput()

(3) 当activity/fragment中具有softInput,弹窗中同样具有softInput时，因为在使用弹窗时实际内部切换不同的模式，建议在
dismiss的时候对activity/fragment进行重置
//底部不向上弹出
requireActivity().setSoftJustPanAndAlwaysHidden()
//底部向上弹出
requireActivity().setSoftJustResizeAndAlwaysHidden()
```












