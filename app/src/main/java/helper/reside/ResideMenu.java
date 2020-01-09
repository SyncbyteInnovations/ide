package helper.reside;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ids.employeeat.R;


/**
 * User: special
 * Date: 13-12-10
 * Time: 下午10:44
 * Mail: specialcyci@gmail.com
 */
@SuppressWarnings("all")
public class ResideMenu extends FrameLayout {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    private static final int PRESSED_MOVE_HORIZONTAL = 2;
    private static final int PRESSED_DOWN = 3;
    private static final int PRESSED_DONE = 4;
    private static final int PRESSED_MOVE_VERTICAL = 5;

    private ImageView imageViewShadow;
    private ImageView imageViewBackground;
    private View leftMenuView;
    private View rightMenuView;
    private View viewMenu;
    private Activity activity;
    private int defaultRightViewId;
    private int rightViewId;
    private View innerView;
    /**
     * The DecorView of current activity.
     */
    private ViewGroup viewDecor;
    private TouchDisableView viewActivity;
    /**
     * The flag of menu opening status.
     */
    private boolean isOpened;
    private float shadowAdjustScaleX;
    private float shadowAdjustScaleY;
    /**
     * Views which need stop to intercept touch events.
     */
    private List<View> ignoredViews;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private OnMenuListener menuListener;
    private float lastRawX;
    private boolean isInIgnoredView = false;
    private int scaleDirection = DIRECTION_LEFT;
    private int pressedState = PRESSED_DOWN;
    private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
    // Valid scale factor is between 0.0f and 1.0f.
    private float scaleValue = 0.5f;

    private boolean mUse3D;
    private static final int ROTATE_Y_ANGLE = 10;

    public ResideMenu(Context context)
    {
        super(context);
    }

    public ResideMenu(Activity activity, int customLeftMenuId, int customRightMenuId)
    {
        super(activity);
        this.activity = activity;
        initViews(customLeftMenuId, customRightMenuId);
    }

    private void initViews(int customLeftMenuId, int customRightMenuId)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        if (inflater != null)
        {
            inflater.inflate(R.layout.drawer_reside_menu, this);

            if (customLeftMenuId >= 0)
            {
                leftMenuView = inflater.inflate(customLeftMenuId, this, false);
            }

            if (customRightMenuId >= 0)
            {
                rightMenuView = inflater.inflate(customRightMenuId, this, false);
                rightViewId = customRightMenuId;
                defaultRightViewId = customRightMenuId;
            }

            imageViewShadow = findViewById(R.id.iv_shadow);
            imageViewBackground = findViewById(R.id.iv_background);

            FrameLayout menuHolder = findViewById(R.id.sv_menu_holder);
            menuHolder.addView(leftMenuView);
            menuHolder.addView(rightMenuView);
        }
    }

    public void updateRightMenuView(int viewId)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        rightViewId = viewId;
        innerView = null;
        rightMenuView = inflater.inflate(viewId, this, false);
    }

    public void updateRightMenuView(int viewId, int innerViewId)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        rightViewId = viewId;
        rightMenuView = inflater.inflate(viewId, this, false);
        innerView = null;
        if (innerViewId > 0)
        {
            innerView = rightMenuView.findViewById(innerViewId);
        }
    }


    /**
     * Returns left menu view so you can findViews and do whatever you want with
     */
    public View getLeftMenuView()
    {
        return leftMenuView;
    }

    /**
     * Returns right menu view so you can findViews and do whatever you want with
     */
    public View getRightMenuView()
    {
        return rightMenuView;
    }

    /*@Override
    protected boolean fitSystemWindows(Rect insets)
    {
        // Applies the content insets to the view's padding, consuming that
        // content (modifying the insets to be 0),
        // and returning true. This behavior is off by default and can be
        // enabled through setFitsSystemWindows(boolean)
        // in api14+ devices.

        // This is added to fix soft navigationBar's overlapping to content above LOLLIPOP
        int bottomPadding = viewActivity.getPaddingBottom() + insets.bottom;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (!hasBackKey || !hasHomeKey)
        {//there's a navigation bar
            bottomPadding += getNavigationBarHeight();
        }

        this.setPadding(viewActivity.getPaddingLeft() + insets.left,
                viewActivity.getPaddingTop() + insets.top,
                viewActivity.getPaddingRight() + insets.right,
                bottomPadding);
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return true;
    }*/

    private int getNavigationBarHeight()
    {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void attachToActivity()
    {
        initValue();
        setShadowAdjustScaleXByOrientation();
        viewDecor.addView(this, 0);
    }

    private void initValue()
    {
        ignoredViews = new ArrayList<>();
        viewDecor = (ViewGroup) activity.getWindow().getDecorView();
        viewActivity = new TouchDisableView(this.activity);

        View context = viewDecor.getChildAt(0);
        viewDecor.removeViewAt(0);
        viewActivity.setContent(context);
        addView(viewActivity);

        ViewGroup parent = (ViewGroup) leftMenuView.getParent();
        parent.removeView(leftMenuView);
        parent.removeView(rightMenuView);
    }

    private void setShadowAdjustScaleXByOrientation()
    {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            shadowAdjustScaleX = 0.034f;
            shadowAdjustScaleY = 0.12f;
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            shadowAdjustScaleX = 0.06f;
            shadowAdjustScaleY = 0.07f;
        }
    }

    /**
     * Set the background image of menu;
     *
     * @param imageResource
     */
    public void setBackground(int imageResource)
    {
        imageViewBackground.setImageResource(imageResource);
    }

    public void setShadowVisible(boolean isVisible)
    {
        if (isVisible)
        {
            imageViewShadow.setBackgroundResource(R.drawable.shadow);
        }
        else
        {
            imageViewShadow.setBackgroundResource(0);
        }
    }


    public void setMenuListener(OnMenuListener menuListener)
    {
        this.menuListener = menuListener;
    }


    public OnMenuListener getMenuListener()
    {
        return menuListener;
    }

    /**
     * Show the menu;
     */
    public void openMenu(int direction)
    {

        setScaleDirection(direction);

        isOpened = true;
        if (innerView != null)
        {
            innerView.setVisibility(VISIBLE);
        }
        AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity, scaleValue, scaleValue);
        AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow, scaleValue + shadowAdjustScaleX, scaleValue + shadowAdjustScaleY);
        AnimatorSet alpha_menu = buildMenuAnimation(viewMenu, 1.0f);
        scaleDown_shadow.addListener(animationListener);
        scaleDown_activity.playTogether(scaleDown_shadow);
        scaleDown_activity.playTogether(alpha_menu);
        scaleDown_activity.start();
    }

    /**
     * Close the menu;
     */
    public void closeMenu()
    {
        isOpened = false;
        if (innerView != null)
        {
            innerView.setVisibility(INVISIBLE);
        }
        AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity, 1.0f, 1.0f);
        AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow, 1.0f, 1.0f);
        AnimatorSet alpha_menu = buildMenuAnimation(viewMenu, 0.0f);
        scaleUp_activity.addListener(animationListener);
        scaleUp_activity.playTogether(scaleUp_shadow);
        scaleUp_activity.playTogether(alpha_menu);
        scaleUp_activity.start();

        if (defaultRightViewId != rightViewId)
        {
            updateRightMenuView(defaultRightViewId);
        }
    }

    @Deprecated
    public void setDirectionDisable(int direction)
    {
        disabledSwipeDirection.add(direction);
    }

    public void setSwipeDirectionDisable(int direction)
    {
        disabledSwipeDirection.add(direction);
    }

    private boolean isInDisableDirection(int direction)
    {
        return disabledSwipeDirection.contains(direction);
    }

    private void setScaleDirection(int direction)
    {

        int screenWidth = getScreenWidth();
        float pivotX;
        float pivotY = getScreenHeight() * 0.5f;

        if (direction == DIRECTION_LEFT)
        {
            viewMenu = leftMenuView;
            pivotX = screenWidth * 1.5f;
        }
        else
        {
            viewMenu = rightMenuView;
            pivotX = screenWidth * -0.5f;
        }

        viewActivity.setPivotX(pivotX);
        viewActivity.setPivotY(pivotY);
        imageViewShadow.setPivotX(pivotX);
        imageViewShadow.setPivotY(pivotY);
        scaleDirection = direction;
    }

    /**
     * return the flag of menu status;
     *
     * @return
     */
    public boolean isOpened()
    {
        return isOpened;
    }

    private OnClickListener viewActivityOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (isOpened())
            {
                closeMenu();
            }
        }
    };

    private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation)
        {
            if (isOpened())
            {
                showScrollViewMenu(viewMenu);
                if (menuListener != null)
                {
                    menuListener.openMenu();
                }
            }
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
            // reset the view;
            if (isOpened())
            {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);
            }
            else
            {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
                hideScrollViewMenu(leftMenuView);
                hideScrollViewMenu(rightMenuView);
                if (menuListener != null)
                {
                    menuListener.closeMenu();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {

        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {

        }
    };

    /**
     * A helper method to build scale down animation;
     *
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     * @return
     */
    private AnimatorSet buildScaleDownAnimation(View target, float targetScaleX, float targetScaleY)
    {

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX), ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

        if (mUse3D)
        {
            int angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
            scaleDown.playTogether(ObjectAnimator.ofFloat(target, "rotationY", angle));
        }
        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(250);
        return scaleDown;
    }

    /**
     * A helper method to build scale up animation;
     *
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     * @return
     */
    private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX, float targetScaleY)
    {

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX), ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

        if (mUse3D)
        {
            scaleUp.playTogether(ObjectAnimator.ofFloat(target, "rotationY", 0));
        }

        scaleUp.setDuration(250);
        return scaleUp;
    }

    private AnimatorSet buildMenuAnimation(View target, float alpha)
    {

        AnimatorSet alphaAnimation = new AnimatorSet();
        alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha", alpha));

        alphaAnimation.setDuration(250);
        return alphaAnimation;
    }

    /**
     * If there were some view you don't want reside menu
     * to intercept their touch event, you could add it to
     * ignored views.
     *
     * @param v
     */
    public void addIgnoredView(View v)
    {
        ignoredViews.add(v);
    }

    /**
     * Remove a view from ignored views;
     *
     * @param v
     */
    public void removeIgnoredView(View v)
    {
        ignoredViews.remove(v);
    }

    /**
     * Clear the ignored view list;
     */
    public void clearIgnoredViewList()
    {
        ignoredViews.clear();
    }

    /**
     * If the motion event was relative to the view
     * which in ignored view list,return true;
     *
     * @param ev
     * @return
     */
    private boolean isInIgnoredView(MotionEvent ev)
    {
        Rect rect = new Rect();
        for (View v : ignoredViews)
        {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
            {
                return true;
            }
        }
        return false;
    }

    private void setScaleDirectionByRawX(float currentRawX)
    {
        if (currentRawX < lastRawX)
        {
            setScaleDirection(DIRECTION_RIGHT);
        }
        else
        {
            setScaleDirection(DIRECTION_LEFT);
        }
    }

    private float getTargetScale(float currentRawX)
    {
        float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
        scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX : scaleFloatX;

        float targetScale = viewActivity.getScaleX() - scaleFloatX;
        targetScale = targetScale > 1.0f ? 1.0f : targetScale;
        targetScale = targetScale < 0.5f ? 0.5f : targetScale;
        return targetScale;
    }

    private float lastActionDownX, lastActionDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        float currentActivityScaleX = viewActivity.getScaleX();
        if (currentActivityScaleX == 1.0f)
        {
            setScaleDirectionByRawX(ev.getRawX());
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastActionDownX = ev.getX();
                lastActionDownY = ev.getY();
                isInIgnoredView = isInIgnoredView(ev) && !isOpened();
                pressedState = PRESSED_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isInIgnoredView || isInDisableDirection(scaleDirection))
                {
                    break;
                }

                if (pressedState != PRESSED_DOWN &&
                        pressedState != PRESSED_MOVE_HORIZONTAL)
                {
                    break;
                }

                int xOffset = (int) (ev.getX() - lastActionDownX);
                int yOffset = (int) (ev.getY() - lastActionDownY);

                if (pressedState == PRESSED_DOWN)
                {
                    if (yOffset > 25 || yOffset < -25)
                    {
                        pressedState = PRESSED_MOVE_VERTICAL;
                        break;
                    }
                    if (xOffset < -50 || xOffset > 50)
                    {
                        pressedState = PRESSED_MOVE_HORIZONTAL;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
                else if (pressedState == PRESSED_MOVE_HORIZONTAL)
                {
                    if (currentActivityScaleX < 0.95)
                    {
                        showScrollViewMenu(viewMenu);
                    }

                    float targetScale = getTargetScale(ev.getRawX());
                    if (mUse3D)
                    {
                        int angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
                        angle *= (1 - targetScale) * 2;
                        viewActivity.setRotationY(angle);

                        imageViewShadow.setScaleX(targetScale - shadowAdjustScaleX);
                        imageViewShadow.setScaleY(targetScale - shadowAdjustScaleY);
                    }
                    else
                    {
                        imageViewShadow.setScaleX(targetScale + shadowAdjustScaleX);
                        imageViewShadow.setScaleY(targetScale + shadowAdjustScaleY);
                    }
                    viewActivity.setScaleX(targetScale);
                    viewActivity.setScaleY(targetScale);
                    viewMenu.setAlpha((1 - targetScale) * 2.0f);

                    lastRawX = ev.getRawX();
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                if (isInIgnoredView)
                {
                    break;
                }
                if (pressedState != PRESSED_MOVE_HORIZONTAL)
                {
                    break;
                }

                pressedState = PRESSED_DONE;
                if (isOpened())
                {
                    if (currentActivityScaleX > 0.56f)
                    {
                        closeMenu();
                    }
                    else
                    {
                        openMenu(scaleDirection);
                    }
                }
                else
                {
                    if (currentActivityScaleX < 0.94f)
                    {
                        openMenu(scaleDirection);
                    }
                    else
                    {
                        closeMenu();
                    }
                }

                break;

        }
        lastRawX = ev.getRawX();
        return super.dispatchTouchEvent(ev);
    }

    public int getScreenHeight()
    {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int getScreenWidth()
    {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setScaleValue(float scaleValue)
    {
        this.scaleValue = scaleValue;
    }

    public void setUse3D(boolean use3D)
    {
        mUse3D = use3D;
    }

    public interface OnMenuListener {
        public void openMenu();

        public void closeMenu();
    }

    private void showScrollViewMenu(View scrollViewMenu)
    {
        if (scrollViewMenu != null && scrollViewMenu.getParent() == null)
        {
            addView(scrollViewMenu);
        }
    }

    private void hideScrollViewMenu(View scrollViewMenu)
    {
        if (scrollViewMenu != null && scrollViewMenu.getParent() != null)
        {
            removeView(scrollViewMenu);
        }
    }
}
