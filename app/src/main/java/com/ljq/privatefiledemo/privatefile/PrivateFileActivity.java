package com.ljq.privatefiledemo.privatefile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljq.privatefiledemo.R;
import com.ljq.privatefiledemo.patternlockerjava.DefaultLockerNormalCellViewJava;
import com.ljq.privatefiledemo.patternlockerjava.DefaultStyleDecoratorJava;
import com.ljq.privatefiledemo.patternlockerjava.OnPatternChangeListenerJava;
import com.ljq.privatefiledemo.patternlockerjava.PatternHelperJava;
import com.ljq.privatefiledemo.patternlockerjava.PatternLockerViewJava;
import com.ljq.privatefiledemo.privatefile.sql.CryptoFileSQLManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Description : 隐私文件页面 包含密码界面和隐私文件列表页面
 * Author      : ljq
 * Date        : 2022/04/25
 */
public class PrivateFileActivity extends AppCompatActivity {
    private static final String TAG = PrivateFileActivity.class.getSimpleName();

    public enum State {
        UNREGISTERED, REGISTERING, WAITING_REG, REGISTERED, ENTRY_PRIVATE
    }

    private PatternHelperJava mPatternHelper;

    private Context mContext;
    private PatternLockerViewJava mPatternLockerView;
    //手势界面
    private ConstraintLayout mRlPattern;
    //隐私文件界面
    private ConstraintLayout mClPrivate;

    //    private PatternIndicatorView mPatternIndicatorView;
    private TextView mTvMsg, mTvTip;
    private Button mBtnReset, mBtnConfirm;
    private RelativeLayout mRlButtons;
    private String mErrorStr;

    private Button mBtnTest;

//    private boolean isNightMode;

    //当前此类处于的阶段
    private State mRegistrationState = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_private_file);
        mContext = this.getBaseContext();
        mPatternHelper = new PatternHelperJava(mContext);
        initRegistrationState();
        initPatternPart();
        initPrivatePart();
        changeUI();
    }

    private void initRegistrationState() {
        if (TextUtils.isEmpty(mPatternHelper.getPwdString())) {//todo md5
            mRegistrationState = State.UNREGISTERED;
        }
        else {
            mRegistrationState = State.REGISTERED;
        }
    }

    private void initPatternPart() {
        findViews();
        initPatternLockView();
        initTvMsg();
        initBtnReset();
        initBtnConfirm();
        initClearPasswordButtonIfDebug();
    }

    private void findViews() {
        mPatternLockerView = findViewById(R.id.pattern_locker_view);
        mTvMsg = findViewById(R.id.text_msg_title);
        mTvTip = findViewById(R.id.text_msg_tip);
        mBtnReset = findViewById(R.id.btn_rest);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mRlButtons = findViewById(R.id.rl_buttons);
        mRlPattern = findViewById(R.id.rl_pattern);
        mClPrivate = findViewById(R.id.cl_private);
    }

    private void initPatternLockView() {
        DefaultLockerNormalCellViewJava cellView =
                (DefaultLockerNormalCellViewJava) mPatternLockerView.getNormalCellView();
        if (cellView != null) {
            DefaultStyleDecoratorJava styleDecorator = cellView.getStyleDecorator();
            styleDecorator.setHitColor(ContextCompat.getColor(mContext,
                    R.color.download_list_pattern_btn_blue));
            styleDecorator.setErrorColor(ContextCompat.getColor(mContext,
                    R.color.download_list_pattern_btn_red));
            styleDecorator.setNormalColor(ContextCompat.getColor(mContext,
                    R.color.download_list_pattern_lock_cell_stroke_color));

            styleDecorator.setFillColor(ContextCompat.getColor(mContext,
                    R.color.download_list_background_color));
        }

        mPatternLockerView.setOnPatternChangedListener(new OnPatternChangeListenerJava() {
            @Override
            public void onStart(@NotNull PatternLockerViewJava view) {

            }

            @Override
            public void onChange(@NotNull PatternLockerViewJava view,
                    @NotNull List<Integer> hitIndexList) {

            }

            @Override
            public void onComplete(@NotNull PatternLockerViewJava view,
                    @NotNull List<Integer> hitIndexList) {

                if (mRegistrationState == State.WAITING_REG) {//等待确认中不对任何手势操作做响应
                    return;
                }
                boolean isOk = isPatternOk(hitIndexList);
                boolean isFinish = mPatternHelper.isFinish();//抽一下
                Log.d(TAG,
                        "mPatternLockerView onComplete mPatternHelper.isOk()" +
                                mPatternHelper.isOk() + " mPatternHelper.isFinish() " +
                                mPatternHelper.isFinish());
                Log.d(TAG,
                        "mPatternLockerView onComplete mPatternHelper.getMessage()" +
                                mPatternHelper.getMessage());
                view.updateStatus(!isOk);
//                mPatternIndicatorView.updateState(hitIndexList, !isOk);
                if (!isOk) {
                    Toast.makeText(mContext, mErrorStr, Toast.LENGTH_SHORT).show();
                    playShakingAnimation();
                }
                else {
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

                }
            }

            @Override
            public void onClear(@NotNull PatternLockerViewJava view) {
                Log.d(TAG, "mPatternLockerView onClear");
                Log.d(TAG, "mPatternLockerView onClear mPatternHelper.isFinish()" +
                        mPatternHelper.isFinish());
            }
        });
    }

    private void initBtnConfirm() {
        mBtnReset.setTextColor(getResources().getColor(R.color.black));
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只有一种情况能点击 就是设置密码确认了
                mPatternHelper.confirm();
                Toast.makeText(mContext,
                        getString(R.string.pattern_locker_tip_successfully),
                        Toast.LENGTH_SHORT)
                       .show();
                changeState();
                //todo 由于设计改动 成功注册后连跳两级
                changeState();
            }
        });
    }

    private void initBtnReset() {
        mBtnReset.setTextColor(getResources().getColor(R.color.black));
        mBtnReset.setOnClickListener(new View.OnClickListener() {//put init
            @Override
            public void onClick(View v) {
                mRegistrationState = State.UNREGISTERED;
                mPatternHelper.reset();
                changeUI();
            }
        });
    }

    private void initTvMsg() {
        mTvMsg.setTextColor(getResources().getColor(R.color.black));
    }


    //test code
    private void initClearPasswordButtonIfDebug() {
        if (DebugConfig.DEBUG) {
            mBtnTest = findViewById(R.id.test_clear_pwd);
            mBtnTest.setVisibility(View.VISIBLE);
            mBtnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRegistrationState = State.UNREGISTERED;
                    mPatternHelper.reset();
                    changeUI();
                }
            });
        }
    }

    private void playShakingAnimation() {
        ObjectAnimator animator1 = ObjectAnimator
                .ofFloat(mPatternLockerView, "translationX", 0, -16)
                .setDuration(83);
        ObjectAnimator animator2 = ObjectAnimator
                .ofFloat(mPatternLockerView, "translationX", 0, 22)
                .setDuration(133);
        ObjectAnimator animator3 = ObjectAnimator
                .ofFloat(mPatternLockerView, "translationX", 0, -12)
                .setDuration(117);
        ObjectAnimator animator4 = ObjectAnimator
                .ofFloat(mPatternLockerView, "translationX", 0, 16)
                .setDuration(117);

        final AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AnticipateInterpolator());
        set.playSequentially(animator1, animator2, animator3, animator4);
        set.start();
    }

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

    private void updateBtnConfirmTextColor() {
        mBtnConfirm.setTextColor(
            mContext.getResources().getColor(R.color.download_list_pattern_btn_gray));
    }

    private boolean isPatternOk(List<Integer> hitIndexList) {
        if (mRegistrationState == State.UNREGISTERED || mRegistrationState == State.REGISTERING) {
            mPatternHelper.validateForSetting(hitIndexList);
        }
        else if (mRegistrationState == State.REGISTERED) {
            mPatternHelper.validateForChecking(hitIndexList);
        }
        return mPatternHelper.isOk();
    }


    /////////////////////上面是手势密码逻辑/////////////////////
    /////////////////////下面是隐私文件逻辑/////////////////////
    private PrivateFileListAdapter mAdapter;
//    private List<PrivateFileItemBean> mDataListBeans;

    /**
     * 触发周期在onCreate
     */
    private void initPrivatePart() {
        initPrivateView();
    }

    private void initPrivateView() {
        initAdapterAndRecyclerView();
        refreshData();
    }


    private void initAdapterAndRecyclerView() {
        mAdapter = new PrivateFileListAdapter(mContext,
                getPrivateFileData());

        RecyclerView mRecyclerView = findViewById(R.id.date_ordered_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnChangeListener(new PrivateFileListAdapter.onChangeListener() {
            @Override
            public void backSuc() {
                Toast.makeText(mContext,"还原成功",Toast.LENGTH_LONG).show();
                refreshData();
            }

            @Override
            public void backFail() {
                Toast.makeText(mContext,"还原失败",Toast.LENGTH_LONG).show();

            }

            @Override
            public void delSuc() {
                Toast.makeText(mContext,"删除成功",Toast.LENGTH_LONG).show();
                refreshData();
            }

            @Override
            public void delFail() {
                Toast.makeText(mContext,"删除失败",Toast.LENGTH_LONG).show();

            }
        });


    }

    private List<PrivateFileItemBean> getPrivateFileData() {
        return CryptoFileSQLManager.queryAllCryptoFileBean(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        initData(HideFileManager.getInstance(this).getAllCryptoFile());
    }

    private void initData(List<PrivateFileItemBean> beans) {
        if (beans != null) {
//            mDataListBeans = beans;
            mAdapter.setData(beans);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showPrivateFileListUI() {
        mRlPattern.setVisibility(View.GONE);
        mClPrivate.setVisibility(View.VISIBLE);
    }

    private void hidePrivateFileListUI() {
        mRlPattern.setVisibility(View.VISIBLE);
        mClPrivate.setVisibility(View.GONE);
    }
}
