package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnSelectInitialActiveMenuItemListener;
import de.symeda.sormas.app.component.menu.PageMenuNavAdapter;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.ConstantHelper;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Orson on 10/12/2017.
 */

public abstract class BaseReadActivity extends AbstractSormasActivity implements IUpdateSubHeadingTitle, OnLandingPageMenuClickListener, OnSelectInitialActiveMenuItemListener {

    private View fragmentFrame = null;
    private View statusFrame = null;
    private View applicationTitleBar = null;
    private BaseReadActivityFragment fragment;
    private TextView subHeadingListActivityTitle;
    private LandingPageMenuControl pageMenu = null;
    private FloatingActionButton fab = null;
    private LandingPageMenuItem activeMenu = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveActiveMenuState(outState, activeMenuKey);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        activeMenuKey = RestoreActiveMenuState(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
        } else {
            activeMenuKey = RestoreActiveMenuState(savedInstanceState);
        }

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);


        subHeadingListActivityTitle = (TextView)findViewById(R.id.subHeadingListActivityTitle);
        fragmentFrame = findViewById(R.id.fragment_frame);
        pageMenu = (LandingPageMenuControl) findViewById(R.id.landingPageMenuControl);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        try {
            if (showPageMenu()) {
                if (pageMenu != null) {
                    Context menuControlContext = this.pageMenu.getContext();

                    pageMenu.setVisibility(View.GONE);
                    pageMenu.setOnLandingPageMenuClickListener(this);
                    pageMenu.setOnSelectInitialActiveMenuItem(this);

                    pageMenu.setAdapter(new PageMenuNavAdapter(menuControlContext));
                    pageMenu.setMenuParser(new LandingPageMenuParser(menuControlContext));
                    pageMenu.setMenuData(getPageMenuData());
                }

                if (fab != null) {
                    //fab.setAlpha(0.5f);
                    fab.setVisibility(View.VISIBLE);
                    setFabUpDrawable();

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (pageMenu.getVisibility() == View.VISIBLE) {
                                pageMenu.setVisibility(View.GONE);
                                setFabUpDrawable();
                            } else {
                                pageMenu.setVisibility(View.VISIBLE);
                                setFabDownDrawable();
                            }
                        }
                    });
                }
            } else {
                if (pageMenu != null) {
                    pageMenu.setVisibility(View.GONE);
                }

                if (fab != null) {
                    fab.setVisibility(View.GONE);
                    setFabDownDrawable();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        if (fragmentFrame != null) {
            try {
                if (savedInstanceState == null) {
                    // setting the fragment_frame
                    BaseReadActivityFragment activeFragment = null;
                    activeFragment = getActiveReadFragment();
                    replaceFragment(activeFragment);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void initializeActivity(Bundle arguments);

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSubHeadingTitle();

        if (applicationTitleBar != null) {
            if (showTitleBar()) {
                applicationTitleBar.setVisibility(View.VISIBLE);

                if (statusFrame != null) {
                    if (showStatusFrame()) {
                        Context statusFrameContext = statusFrame.getContext();

                        Drawable drw = (Drawable) ContextCompat.getDrawable(statusFrameContext, R.drawable.indicator_status_circle);
                        drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource(statusFrameContext)), PorterDuff.Mode.SRC);

                        TextView txtStatusName = (TextView)statusFrame.findViewById(R.id.txtStatusName);
                        ImageView imgStatus = (ImageView)statusFrame.findViewById(R.id.statusIcon);


                        txtStatusName.setText(getStatusName(statusFrameContext));
                        imgStatus.setBackground(drw);

                        statusFrame.setVisibility(View.VISIBLE);
                    } else {
                        statusFrame.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            applicationTitleBar.setVisibility(View.GONE);
        }

        /*try {
            if (this.pageMenu != null && showPageMenu()) {

                Context menuControlContext = this.pageMenu.getContext();

                this.pageMenu.setAdapter(new PageMenuNavAdapter(menuControlContext));
                this.pageMenu.setMenuParser(new LandingPageMenuParser(menuControlContext));
                this.pageMenu.setMenuData(getPageMenuData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }*/


        fragment.onResume();
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null)? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    public void updateSubHeadingTitle() {
        String subHeadingTitle = "";
        LandingPageMenuItem activeMenu = getActiveMenuItem();

        if (fragment != null) {
            subHeadingTitle = (activeMenu == null)? fragment.getSubHeadingTitle() : activeMenu.getTitle();
        }

        setSubHeadingTitle(subHeadingTitle);
    }

    @Override
    public void updateSubHeadingTitle(int titleResource) {
        setSubHeadingTitle(getApplicationContext().getResources().getString(titleResource));
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    @Override
    public LandingPageMenuItem onSelectInitialActiveMenuItem(ArrayList<LandingPageMenuItem> menuList) {
        activeMenu = menuList.get(0);

        for(LandingPageMenuItem m: menuList){
            if (m.getKey() == activeMenuKey){
                activeMenu = m;
            }
        }

        return activeMenu;
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    public String getStatusName(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    public int getStatusColorResource(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public void replaceFragment(BaseReadActivityFragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = f;

        if (fragment != null) {
            fragment.setArguments(getIntent().getExtras());
            ft.replace(R.id.fragment_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void setFabUpDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_keyboard_arrow_up_36dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }

    public void setFabDownDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_keyboard_arrow_down_36dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }

    protected static <TActivity extends AbstractSormasActivity, TCapsule extends INavigationCapsule>
    void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        //AbstractDomainObject record = dataCapsule.getRecord();

        Intent intent = new Intent(fromActivity, toActivity);
        intent.putExtra(ConstantHelper.KEY_DATA_UUID, dataUuid);

        if (filterStatus != null)
            intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, filterStatus.getValue());

        if (pageStatus != null)
            intent.putExtra(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        /*if (record != null)
            intent.putExtra(ConstantHelper.ARG_PAGE_RECORD, record);*/

        /*for (IStatusElaborator e: dataCapsule.getOtherStatus()) {
            if (e != null)
                intent.putExtra(e.getStatekey(), e.getValue());
        }*/
        fromActivity.startActivity(intent);
    }

    public abstract BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException;

    protected void setActiveMenu(LandingPageMenuItem menuItem) {
        activeMenu = menuItem;
        activeMenuKey = menuItem.getKey();
    }

    public LandingPageMenuItem getActiveMenuItem() {
        return activeMenu;
    }

    public abstract boolean showStatusFrame();

    public abstract boolean showTitleBar();

    public abstract boolean showPageMenu();

    public abstract Enum getPageStatus();

    public abstract String getPageMenuData();

    public abstract boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException;

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getPageStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_PAGE_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getArgByElaboratorKey(Bundle arguments, String key) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(key)) {
                e = (E) arguments.getSerializable(key);
            }
        }

        return e;
    }



    protected <E extends Enum<E>> void SaveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected <E extends Enum<E>> void SavePageStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_STATUS, status);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void SaveActiveMenuState(Bundle outState, int activeMenuKey) {
        if (outState != null) {
            outState.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        }
    }



    private <E extends Enum<E>> E RestoreFilterStatusState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return (E)savedInstanceState.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
        }

        return null;
    }

    private <E extends Enum<E>> E RestorePageStatusState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return (E)savedInstanceState.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
        }

        return null;
    }

    private String RestoreRecordUuidState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState.getString(ConstantHelper.KEY_DATA_UUID);
        }

        return null;
    }

    private int RestoreActiveMenuState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState.getInt(ConstantHelper.KEY_ACTIVE_MENU);
        }

        return -1;
    }

}
