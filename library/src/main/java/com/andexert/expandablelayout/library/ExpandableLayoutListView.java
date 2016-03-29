/***********************************************************************************
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Robin Chutaux
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package com.andexert.expandablelayout.library;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Author :    Chutaux Robin
 * Date :      9/17/2014
 */
public class ExpandableLayoutListView extends ListView {
    private Integer position = -1;
    private boolean isItemCollapsing;// is some item is collapsing at that time

    public ExpandableLayoutListView(Context context) {
        super(context);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    public ExpandableLayoutListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    public ExpandableLayoutListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    @Override
    public boolean performItemClick(final View view, int position, long id) {
        this.position = position;

        for (int index = 0; index < getChildCount(); ++index) {
            if (index != (position - getFirstVisiblePosition())) {
                ExpandableLayoutItem currentExpandableLayout = (ExpandableLayoutItem) getChildAt(index).findViewWithTag(ExpandableLayoutItem.class.getName());
                if (currentExpandableLayout != null) {
                    if (currentExpandableLayout.isOpened()) {
                        currentExpandableLayout.hide();
                        isItemCollapsing = true;
                    }
                }
            }
        }

        final ExpandableLayoutItem expandableLayout = (ExpandableLayoutItem) getChildAt(position - getFirstVisiblePosition()).findViewWithTag(ExpandableLayoutItem.class.getName());
        if (expandableLayout != null) {
            if (expandableLayout.isOpened()) {
                expandableLayout.hide();
                isItemCollapsing = true;
            } else {
                // TODO refactor
                final int headerHeight = view.getHeight();
                int contentHeight = expandableLayout.getContentHeight();
                final int expandedHeight = headerHeight + contentHeight;
                expandableLayout.setOnViewExpandedCommand(new Runnable() {
                    @Override
                    public void run() {
                        Rect expandedRect = new Rect();
                        expandableLayout.getGlobalVisibleRect(expandedRect);

                        Rect containerRect = new Rect();
                        getGlobalVisibleRect(containerRect);

                        int offset = (expandedRect.top + expandedHeight) - containerRect.bottom;

                        //int offset = expandedHeight - (expandedRect.bottom - expandedRect.top);
                        if (offset > 0)
                            smoothScrollBy(offset, expandableLayout.getDuration());
                        expandableLayout.setOnViewExpandedCommand(null);
                    }
                });
                int scrollSize = calculateBottomScroll(expandableLayout);
                expandableLayout.show();
                if (scrollSize > 0)
                    smoothScrollBy(scrollSize, expandableLayout.getDuration());
                isItemCollapsing = false;
            }
        }
        return super.performItemClick(view, position, id);
    }

    private int calculateBottomScroll(ExpandableLayoutItem layoutItem) {
        int headerHeight = layoutItem.getHeight();
        Rect headerRect = new Rect();
        Rect containerRect = new Rect();
        layoutItem.getGlobalVisibleRect(headerRect);
        getGlobalVisibleRect(containerRect);

        int scrollSize;
        if (isItemCollapsing) {
            scrollSize = headerHeight - (headerRect.bottom - headerRect.top);
        } else {
            int availSpace = containerRect.bottom - headerRect.top;
            int contentHeight = layoutItem.getContentHeight();
            int expandedHeight = headerHeight + contentHeight;
            scrollSize = expandedHeight - availSpace;
        }
        return scrollSize;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (!(l instanceof OnExpandableLayoutScrollListener))
            throw new IllegalArgumentException("OnScrollListner must be an OnExpandableLayoutScrollListener");

        super.setOnScrollListener(l);
    }

    public class OnExpandableLayoutScrollListener implements OnScrollListener {
        private int scrollState = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            this.scrollState = scrollState;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (scrollState != SCROLL_STATE_IDLE) {
                for (int index = 0; index < getChildCount(); ++index) {
                    ExpandableLayoutItem currentExpandableLayout = (ExpandableLayoutItem) getChildAt(index).findViewWithTag(ExpandableLayoutItem.class.getName());
                    if (currentExpandableLayout == null)
                        return;// otherwise NPE if header added
                    if (currentExpandableLayout.isOpened() && index != (position - getFirstVisiblePosition())) {
                        currentExpandableLayout.hideNow();
                    } else if (!currentExpandableLayout.getCloseByUser() && !currentExpandableLayout.isOpened() && index == (position - getFirstVisiblePosition())) {
                        currentExpandableLayout.showNow();
                    }
                }
            }
        }
    }
}
