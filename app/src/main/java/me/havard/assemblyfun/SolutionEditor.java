package me.havard.assemblyfun;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SolutionEditor extends FragmentActivity implements TabLayout.OnTabSelectedListener{

    public static final String EXTRAS_SOLUTION_ID = "solutionId";
    public static final String EXTRAS_TASK_ID = "taskId";

    public static final int NUM_PAGES = 2;
    public static final int TASK_PAGE = 0;
    public static final int SOLUTION_PAGE=1;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private long mSolutionId;
    private long mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_editor);

        mTabLayout = (TabLayout) findViewById(R.id.solution_editor_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.solution_editor_view_pager);
        mPagerAdapter = new TaskSolutionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(SOLUTION_PAGE);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mTabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            Log.e("Assembly Fun", "No extras (solution and task id) were passed to the SolutionEditor");
            return;
        }
        mSolutionId = extras.getLong(EXTRAS_SOLUTION_ID, -1);
        mTaskId = extras.getLong(EXTRAS_TASK_ID, -1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem m) {

        int id = m.getItemId();

        if(id==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(m);
    }

    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() != 0)
            mViewPager.setCurrentItem(0, true);
        else
            super.onBackPressed();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("Assembly Fun", "onTabSelected()");
        mViewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    class TaskSolutionPagerAdapter extends FragmentPagerAdapter {

        public TaskSolutionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putLong(EXTRAS_SOLUTION_ID, mSolutionId);
            args.putLong(EXTRAS_TASK_ID, mTaskId);
            if(position == SOLUTION_PAGE) {
                EditorSolutionFragment fragment = new EditorSolutionFragment();
                fragment.setArguments(args);
                return fragment;
            } else if(position == TASK_PAGE) {
                EditorTaskFragment fragment = new EditorTaskFragment();
                fragment.setArguments(args);
                return fragment;
            }

            Log.e("Assembly Fun", "The TaskSolutionAdapter doesn't have a page with position " + position);
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public String getPageTitle(int position)
        {
            if(position>=0&position<2)
                return getResources().getString(position==0?R.string.title_solution_editor_task_tab:R.string.title_solution_editor_solve_tab);
            return "unknown title";
        }
    }

    public static class EditorSolutionFragment extends Fragment {

        private long mSolutionId, mTaskId;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_editor_solution_page, container, false);

            Bundle args = getArguments();
            if(args!=null){
                mSolutionId = args.getLong(EXTRAS_SOLUTION_ID, -1);
                mTaskId = args.getLong(EXTRAS_TASK_ID, -1);
            }
            return view;
        }
    }

    public static class EditorTaskFragment extends Fragment {

        private long mSolutionId, mTaskId;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_editor_task_page, container, false);

            Bundle args = getArguments();
            if(args!=null){
                mSolutionId = args.getLong(EXTRAS_SOLUTION_ID, -1);
                mTaskId = args.getLong(EXTRAS_TASK_ID, -1);
            }
            return view;
        }
    }
}
