# 需求场景
之前看到某某浏览器有一个下载文件隐藏功能，大家都懂的，当然除了一些自己爱好文件不想让人看到以外 ，可能自己关注的一些东西，下载的资料也不想别人注意到，所以隐藏文件的功能在很多浏览器和下载软件里都能看到，我们现在就从零开始分析和做出来这一套系统。

![微信图片_20220509181806.jpg](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c528dfb655e74398a0c6914082ce78fd~tplv-k3u1fbpfcp-watermark.image?)
![微信图片_20220509181813.jpg](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1473977d4c944b02b25cc4605c764491~tplv-k3u1fbpfcp-watermark.image?)
![微信图片_20220509183551.jpg](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3b04096c09864fcfbb8ad8493ace1485~tplv-k3u1fbpfcp-watermark.image?)
![微信图片_20220509184100.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5dd9ecbdbf80406dafc539501600894a~tplv-k3u1fbpfcp-watermark.image?)

结合某某浏览器可以看到：
1. 下载完成的文件才有隐藏文件权限
2. 隐藏文件呢会在专门的隐藏文件页面进行显示
3. 显示页面里分了类型和时间排列
4. 长按隐私文件会看到菜单选项，有还原和删除
5. 隐私文件可以设置手势密码

我们抓住核心的需求做出来这样一个相似的系统
实现需求1245

现在开始吧

# 实现思路

首先我们要观察其他软件是怎么实现的  
这里需要用到一个工具类  
FileObserver  
主要用来提供对文件或者文件夹的监控  
使用方法参考文章https://www.jianshu.com/p/8f47c699b742  

为什么要使用这个类呢 我们通过这个类监听整个文件系统  
然后打印日志  
这样在系统使用隐藏文件功能时  
哪些文件在哪里被移除 哪些文件在哪里被添加 以及哪些文件在过程中被修改了  
移除的文件很明显就是被隐藏的文件  
而添加的位置就是可以看到隐藏文件夹的位置选择，这里涉及到储存策略和加密策略的问题  
修改的位置则是数据库和配置文件
我们把数据库copy出来 大概有这些参数

| 字段 | 含义 | 作用描述 |
| --- | --- | --- |
| ID | ID | 索引|
| EXTRA_STR | 格外字段 | 存储其他数据的重要信息 视具体情况而定 |
| FILE_NAME | 文件名  | 用于显示参数 |
| FILE_ORIG_PATH | 文件原始路径 | 用于还原文件 |
| FILE_STORE_PATH | 文件现在路径 | 用于操作文件 |
| FILE_SUFFIX | 文件后缀 | 用于显示和操作文件 |
| FILE_ADD_TIME | 文件添加时间 | 用于显示参数 |
| FILE_SIZE | 文件大小 | 用于显示参数 |


在添加的位置上没有使用在/Android/Data/文件夹下 文件名是以/.xxx/开头 且生成文件的文件名为一串无规律数字没有文件后缀

这说明几个问题
- 说明文件并没有放在应用的私有目录下存储 且对一般查看隐藏 也对Android自带的MediaScaner隐藏 里面的内容不会被系统数据库和应用查看到
- 文件名也进行了加密 去掉了后缀名和名称 这样就算想打开也不知道用什么应用打开合适
- 文件没有存储在应用目录下 这样的话删除应用文件依然存在 这可能会给用户造成数据污染 但是同时也不会让用户删除数据后误删隐私文件 这里有个取舍的问题 同时数据库文件也储存在这个文件夹 也是同样的道理 因为应用的数据库会随着应用删除而被删掉 估计这里是应用内部固定写入了文件夹读取的位置 这样能达到任何时候 都能根据配置文件查看到和使用文件

看这里大概初步的实现思路已经分析完成

简单来说 隐私文件夹是以把文件转移到某个固定文件夹 并且采用一定的加密方法存储 同时使用数据库保存转移信息
配合应用逻辑实现隐私文件的功能

这里我自己做的时候就不做应用外的储存了，会少增加一些和隐私文件不核心的代码逻辑，也可以让数据和应用更紧密，不会有脏数据的可能

关于Android文件存储的问题可以参考这篇文章 [一篇文章搞懂android存储目录结构](https://juejin.cn/post/6844904013515718664)

# 实现工具

这里讲的就是实现需要用到的轮子

实现此功能需要三个大的模块

1.下载模块 2.手势密码模块 3.隐私文件模块

我们讲的是隐私模块的实现，而不需要去花精力做其他两个复杂模块

这里选择使用github的两个项目

分别是[Aria](https://github.com/AriaLyy/Aria)下载模块

[PatternLocker](https://github.com/ihsg/PatternLocker)手势密码模块

关于这两块的代码就不细讲了 我也copy了一些这些项目的代码到自己项目方便研究修改

如果自己写项目直接使用这两个模块的API就好

# 设计图

隐私操作的操作流程图

![隐私文件流程图.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0719b6eb09fe4eab80f064b314036a04~tplv-k3u1fbpfcp-watermark.image?)

根据流程图设计的类图

![隐私文件类图.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2ba985a5d34540c2956f78360791a38d~tplv-k3u1fbpfcp-watermark.image?)
（图中名称方法可能和代码有一些出入 但是不影响）

隐私文件系统文件系统主要是由这几个类组成

CryptoFileBean 

隐私文件bean 没有太多要讲的

CryptoFileSQLiteHelper

创建sql数据库的类 没有太多要讲的

CryptoFileSQLiteManager

隐私文件数据库操作类 涉及到各式各样的增删改查

ICryptoFileManager

隐私文件逻辑操作接口 定义设计上需要的方法

HideFileListener

结果返回接口 给UI层面去调用

HideFileManager

隐私文件逻辑操作现实类 

FileUtil

实现文件逻辑的工作 包括 转移 加密 删除 文件等操作

这一套系统符合低耦合高聚合的设计 可以嵌入到任何外在的系统里

只需要 HideFileManager 的实现里做一些修改就行

# 实现细节

## 文件操作

首先考虑的是隐藏文件时的文件操作

### 第一考虑的是文件传输操作

因为在具体的业务场景里 经常会隐藏 1080P的视频等等的大文件

所以采用JAVA的文件流传输是不现实 不能让用户等待超过10秒钟以上

怎么办呢 Java提供了另一种方式进行文件传输 刚好符合我们的需求 就是File的renameto方式 它并不是方法名上看起来的只是改名而已 是可以连着路径一起改变的 可以把它看作move方法

```
    public static void pushFile(Context context, PrivateFileItemBean bean) throws Exception {

//        long start = System.currentTimeMillis();
        String[] result = genderCryptoFilePath(context, bean.getFileOriginalPath());
        bean.setFilePath(result[0]);
        bean.setFileSuffix(result[1]);

        String source = bean.getFileOriginalPath();
        String dest = bean.getFilePath();
        File file = new File(source);
        file.renameTo(new File(dest));

        long end = System.currentTimeMillis();
//        Log.d(TAG,
//                "copyFileUsingFileChannels  " + " spend time " + (end - start) + " source " +
//                        source + " dest " + dest);

//        bean.setFileAddTime(end);
    }
```

这个方法在使用时是10ms以下的速度 可以说是非常理想了

### 第二是加密方法

```
/**
 * 根据原地址生成存储地址 //todo 分离成两个方法
 *
 * @param context
 * @param path
 * @return 数组第一个值为存储地址 第二值为扩展名
 */
public static String[] genderCryptoFilePath(@NonNull Context context, @NonNull String path) {
    String destFolder = context.getExternalFilesDir(".CryptoFiles").toString();
    Log.d(TAG, "genderCryptoFilePath destFolder " + destFolder);

    String fileAllName = path.substring(path.lastIndexOf("/") + 1, path.length());
    String fileName = fileAllName;
    String fileSuffix = "";
    if (fileAllName.contains(".")) {
        fileName = fileAllName.substring(0, fileAllName.lastIndexOf("."));
        fileSuffix =
                fileAllName.substring(fileAllName.lastIndexOf(".") + 1, fileAllName.length());
    }
    File file = new File(destFolder);
    if (!file.exists()) {
        file.mkdirs();
    }
    String destPath = destFolder + "/" + fileName.hashCode();
    Log.d(TAG, "genderCryptoFilePath destPath " + destPath);
    String[] strings = new String[]{destPath, fileSuffix};
    return strings;
}
```

加密方式是采用之前谈到的文件名无意义加上隐藏后缀名的方法

用名称的hash码替换文件名

后缀名通过返回值最终存储到数据库里面 在列表显示文件时 不是显示真实的文件名 而是数据库里的原文件名和后缀名 在隐藏文件的同时 方便用户辨认

## 隐藏文件操作

隐藏文件的具体实现都在HideFileManager里面

```
@Override
public void hideSingleFile(@NonNull PrivateFileItemBean bean,
        @Nullable HideFileListener listener) {
    try {
        //1.转移文件
        FileUtil.pushFile(mContext, bean);
        ArrayList<PrivateFileItemBean> result = new ArrayList<>();
        result.add(bean);
        //2.插入或者更新数据库数据
        CryptoFileSQLManager.insertOrUpdateBeanSingle(mContext,bean);
        //3.插入或者更新辅助数据库数据
        HideFileSupportFileSQLManager.insertOrUpdateHideFileSupportSingle(mContext,bean.getOriginalId()+"",true);
        //4.回调结果
        if (listener != null) {
            listener.OnSuccess(result);
        }
    } catch (Exception e) {
        e.printStackTrace();
        if (listener != null) {
            listener.onFailed(e, "hideSingleFile error");
        }
    }
}
```

可以看到一次隐藏数据的过程中 涉及四个步骤

第一个步骤涉及到文件操作 FileUtil 类

第二三个操作涉及数据库的写入 CryptoFileSQLManager HideFileSupportFileSQLManager 类

第四个操作回调给相应类 一般是UI页面 通知刷新

这个方法可以结合上面的类图看到 HideFileManager只是一个调用的发起和逻辑聚合 具体的操作都是交给相应的类去解决 自己不和底层操作发生关系 以便在需要的时候可以独自替换相应的更有效率更合适的底层实现

这样就完成了一次隐藏操作 那么这个HideFileSupportFileSQLManager是干嘛的呢

#### HideFileSupportFileSQLManager

这个类是以具体的下载记录ID为关键值去标记下载记录是否隐藏 

因为开始设计是直接在下载文件转为隐私文件时将文件记录删除 然后在还原时添加回下载记录 但是这么做有两个问题

第一是要还原下载记录 需要是隐私文件里储存的格外信息过多 而且还要设计一组据转化方法 且不利于把下载系统和隐私文件分开 产生了强耦合

第二是还有一个问题 就是隐藏下载列表文件以后 再去下载相同的文件 会产生新的一条下载记录 如果这时候把之前的隐私文件还原 则会产生两条相同几乎的数据 使用户困惑

所以这里采用另一种实现方式

首先构造一张数据表

| 字段 | 含义 | 作用描述 |
| --- | --- | --- |
| ID | ID  | 索引 |
| FILE_ID | 下载文件记录ID | 用于标记文件 |
| IS_HIDE | 下载文件记录是否隐藏 | 用于标记文件 |

```
@Download.onTaskStart void taskStart(DownloadTask task) {
  mAdapter.updateBtState(task.getKey(), false);
  //开始以后将隐藏文件标识去除
  HideFileSupportFileSQLManager.insertOrUpdateHideFileSupportSingle(this,task.getDownloadEntity().getId()+"",false);
  Log.d(TAG, "taskStart " + task.getDownloadEntity().getId()+"");
}
```

```
public boolean unhideDownloadFile(@NonNull List<Long> ids) {
    for (Long id : ids) {
        HideFileSupportFileSQLManager.updateHideFileSupportSingle(mContext,id+"",false);
    }
    return true;
}
```

使用方法是在下载时和还原文件将下载记录的ID传入并标记为IS_HIDE赋值为false

```
case IEntity.STATE_COMPLETE:
  Log.d(TAG, "任务已完成");
  hideFile(entity);
  break;
```

在隐藏文件的时候赋值为true

在显示列表时 读取数据表 将IS_HIDE为true的数据隐藏

这样就在保有之前的数据的情况下对于用户来说记录是删除的不可见的 也解决上面两个问题


## 还原文件的操作

和隐藏文件逻辑类似 这里就直接贴代码了

```
@Override
public void unHideFiles(List<PrivateFileItemBean> beans, HideFileListener listener) {
    for (PrivateFileItemBean bean : beans) {
        try {
            //1.转移文件
            FileUtil.pullFile(bean);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailed(e, "hideFiles error");
                return;
            }
        }
    }
    List<Long> ids = new ArrayList<>();
    for (PrivateFileItemBean bean : beans) {
        try {
            ids.add(Long.valueOf(bean.getExtraStr()));
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailed(e, "hideFiles error");
                return;
            }
        }
    }
    //2.标记记录
    unhideDownloadFile(ids);
    //3.删除数据库数据
    CryptoFileSQLManager.deleteBeanList(mContext, beans);
    //4.回调结果
    if (listener != null) {
        listener.OnSuccess(beans);
    }
}
```

## 手势密码

![Screenshot_20220510_144705.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/55204681ed1c4a16b5129b52ac8e3e48~tplv-k3u1fbpfcp-watermark.image?)

隐私页面主要分成两个部分 手势密码和隐私文件列表

> 列表这里不多讲 因为暂时就是单纯的UI显示 目前每个选项上只有两个操作选项 还原和删除 其他的类似打开文件功能后续有时间会添加并更新文档和项目

![手势解锁流程图(1).jpg](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0a8e1767c97a4db380664cb142586e47~tplv-k3u1fbpfcp-watermark.image?)

而手势密码页经过逻辑分析可知在整个操作过程有有明显的状态变化 所以这里使用状态机设计模式去编程

分成 这几种状态

```
public enum State {
    UNREGISTERED, REGISTERING, WAITING_REG, REGISTERED, ENTRY_PRIVATE
}
```

1未注册 2注册中 3等待注册 4注册完成 5进入隐私页

其中123是第一次注册环节需要的

如果进行注册过 则直接从4阶段开始 验证密码

验证密码后就进入5阶段 这时手势密码UI都进行了隐藏

当页面被切出或者息屏后 又会在onPause重置为4阶段 以保证私密性


```
@Override
public void onPause() {
    super.onPause();
    if (TextUtils.isEmpty(mPatternHelper.getPwdString())) {
        mRegistrationState = State.UNREGISTERED;
    }
    else {
        mRegistrationState = State.REGISTERED;
    }
    changeUI();
}
```

如上图所示 在完成一些关键步骤以后 会出发changeState方法 推进状态进入下一级 并同时将相关的UI改变 如第一次输入成功，确认成功，和密码输入正确时等等

```
                    if (mRegistrationState == State.UNREGISTERED) {
                        changeState();
                    }
                    else if (mRegistrationState == State.REGISTERING && isFinish) {
                        //这个代码可以抽到changUI
                        mBtnConfirm.setClickable(true);
//                        mBtnConfirm.setBackground(mContext.getDrawable(
//                                R.drawable.download_list_private_button_bg_blue));


                        mBtnConfirm.setBackgroundColor(getColor(R.color.download_list_pattern_btn_blue));

                        mBtnConfirm.setTextColor(mContext.getResources().getColor(R.color.white));
                        mPatternLockerView.setEnabled(false);
                        mPatternLockerView.setEnableAutoClean(false);
                        changeState();
                    }
                    else if (mRegistrationState == State.REGISTERED && isFinish) {
                        changeState();
                    }
```

```
/**
 * 改变此页面的状态
 */
private void changeState() {
    if (mRegistrationState == State.UNREGISTERED) {
        mRegistrationState = State.REGISTERING;
        changeUI();
        return;
    }
    if (mRegistrationState == State.REGISTERING) {
        mRegistrationState = State.WAITING_REG;
        changeUI();
        return;
    }
    if (mRegistrationState == State.WAITING_REG) {
        mRegistrationState = State.REGISTERED;
        changeUI();
        return;
    }
    if (mRegistrationState == State.REGISTERED) {
        mRegistrationState = State.ENTRY_PRIVATE;
        changeUI();
    }
}
```

```
    /**
     * 根据状态改变UI
     */
    private void changeUI() {
        if (mRegistrationState == State.UNREGISTERED) {
//            if(mDownloadCommonViewModel.isCurPage(DownloadListActivity.PRIVATE_FILE_FRAGMENT_INDEX)){
//                mDownloadCommonViewModel.setUnableEdit(true);
//            }
            hidePrivateFileListUI();
            mRlButtons.setVisibility(View.GONE);
            mPatternLockerView.setEnabled(true);
            mPatternLockerView.setEnableAutoClean(true);
            mPatternLockerView.clearHitState();

//            mBtnConfirm.setBackground(
//                    mContext.getDrawable(R.drawable.download_list_private_button_bg_gray));

            mBtnConfirm.setBackgroundColor(getColor(R.color.colorPrimary));

            updateBtnConfirmTextColor();
            mBtnConfirm.setClickable(false);
            mTvMsg.setText(R.string.pattern_locker_first_tip_main);
            mTvTip.setText(R.string.pattern_locker_first_tip_sub);
            mErrorStr = getString(R.string.pattern_locker_first_tip_error);
        }
        if (mRegistrationState == State.REGISTERING) {
            hidePrivateFileListUI();
            mRlButtons.setVisibility(View.VISIBLE);
            mTvTip.setText(R.string.pattern_locker_second_tip_sub);
            mErrorStr = getString(R.string.pattern_locker_second_tip_error);

            mBtnConfirm.setClickable(false);
        }

        if (mRegistrationState == State.WAITING_REG) {
            hidePrivateFileListUI();
            mBtnConfirm.setClickable(true);
        }

        if (mRegistrationState == State.REGISTERED) {
//            if(mDownloadCommonViewModel.isCurPage(DownloadListActivity.PRIVATE_FILE_FRAGMENT_INDEX)){
//                mDownloadCommonViewModel.setUnableEdit(true);
//            }
            mPatternLockerView.setEnabled(true);
            mPatternLockerView.setEnableAutoClean(true);
            mPatternLockerView.clearHitState();
            mRlButtons.setVisibility(View.GONE);
            hidePrivateFileListUI();
            mTvMsg.setText(R.string.pattern_locker_three_tip_main);
            mTvTip.setText("");
            mErrorStr = getString(R.string.pattern_locker_three_tip_error);
        }

        if (mRegistrationState == State.ENTRY_PRIVATE) {//显示隐私文件页面
//            mDownloadCommonViewModel.setUnableEdit(false);
            showPrivateFileListUI();
        }
    }
```

这里具体的代码编写思路还要结合PatternLocker的使用 有疑问的话还需要看下PatternLocker的源码 这里就不多做介绍了

同时我还提供了一个测试按钮

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c926b8f273e54a3aa45f4b4d5f1ca25f~tplv-k3u1fbpfcp-watermark.image?)

可以在手势密码的任何阶段重置到初始阶段 方便大家测试和理解过程

# 结语

整个程序的逻辑大致就是这样的 我会持续优化此文章和项目的一些描述，功能和代码 

感谢观看
















