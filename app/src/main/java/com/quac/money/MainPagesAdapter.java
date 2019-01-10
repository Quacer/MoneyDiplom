package com.quac.money;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagesAdapter extends FragmentPagerAdapter {


    public static final int PAGE_INCOMES=0;
    public static final int PAGE_EXPENSES=1;
    public static final int PAGE_BALANCE=2;
    private String[] titles;
    public MainPagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles=context.getResources().getStringArray(R.array.tab_title);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case PAGE_INCOMES: {
                return ItemsFragment.CreateFr(Item.TYPE_INCOMES);

            }
            case PAGE_EXPENSES: {

                return  ItemsFragment.CreateFr(Item.TYPE_EXPENSES);
            }
            case PAGE_BALANCE: {
                return  new BalanceFragment();
            }
                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

   return titles[position];



    }
}
