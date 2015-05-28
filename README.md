# Android-NiceTab
A nice tab to navigate between the different pages of a ViewPager, supports badge, blur, and cross fade effect.

![NiceTab Demo Gif](https://raw.githubusercontent.com/RobotAmiee/Android-NiceTab/master/art/demo.gif)

# Usage

*For a working implementation of this project see the `demo/` folder.*

  1. Add the library as a project. or just

     ```groovy
     dependencies {
         compile 'me.amiee:nicetab:1.0.0'
     }
     ````

  2. Include the NiceTabLayout widget in your layout. This should usually be placed
     above the `ViewPager` it represents.

        <me.amiee.nicetab.NiceTabLayout
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:id="@+id/sliding_tabs"
            android:layout_width="match_parent"
            android:layout_height="@dimen/tab_height"
            android:background="?colorPrimary"
            app:ntlDividerPaddingLeft="8dp"
            app:ntlDividerPaddingRight="8dp"
            app:ntlIndicatorHeight="2dp"
            app:ntlOverlayColor="#aa1a237e"
            app:ntlTabMode="both"
            app:ntlTextSize="@dimen/tab_title_text_size"
            app:ntlTextStyle="normal"
            app:ntlUnderlineHeight="1dp"/>

  3. In your `onCreate` method (or `onCreateView` for a fragment), bind the
     widget to the `ViewPager`.

         // Initialize the ViewPager and set an adapter
         mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
         mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));

         // Bind the tabs to the ViewPager
         mNiceTabLayout = (NiceTabLayout) view.findViewById(R.id.sliding_tabs);
         mNiceTabLayout.setViewPager(mViewPager);

  4. *(Optional)* If you use an `OnPageChangeListener` with your view pager
     you should set it in the widget rather than on the pager directly.

         // continued from above
         mNiceTabLayout.setOnPageChangeListener(mPageChangeListener);

  5. *(Optional)* If your adapter implements the interface `NiceTabLayout.IconTabProvider` you can add icon to tab view/s.
     The tab view should be a `Textview` or `ImageView` the icon will be used for textView's drawable top or
     imageView's image.

# Customization

        <attr name="ntlTabDistributeEvenly" format="boolean"/>
        <attr name="ntlDrawOrder" format="enum">
            <enum name="indicatorUnderlineDivider" value="0"/>
            <enum name="indicatorDividerUnderline" value="1"/>
            <enum name="underlineIndicatorDivider" value="2"/>
            <enum name="underlineDividerIndicator" value="3"/>
            <enum name="dividerIndicatorUnderline" value="4"/>
            <enum name="dividerUnderlineIndicator" value="5"/>
        </attr>

        <attr name="ntlTabMode" format="enum">
            <!-- Show title only -->
            <enum name="titleOnly" value="0"/>
            <!-- Show icon only(notice: your pager adapter must implements NiceTabLayout.IconProvider) -->
            <enum name="iconOnly" value="1"/>
            <!-- Show both title and icon(notice: your pager adapter must implements NiceTabLayout.IconProvider) -->
            <enum name="both" value="2"/>
        </attr>
        <attr name="ntlTabColorBlendMode" format="enum">
            <enum name="none" value="0"/>
            <!-- Default color and selected color -->
            <enum name="defaultSelected" value="1"/>
            <!-- Selected color and next selected color -->
            <enum name="nextSelected" value="2"/>
        </attr>
        <attr name="ntlTabBackground" format="reference|color"/>
        <attr name="ntlTabOffset" format="dimension"/>
        <attr name="ntlTabSelectedCenter" format="boolean"/>

        <!-- Underline attrs -->
        <attr name="ntlShowUnderline" format="boolean"/>
        <attr name="ntlUnderlineInFront" format="boolean"/>
        <attr name="ntlUnderlineGravity" format="enum">
            <enum name="top" value="0"/>
            <enum name="bottom" value="1"/>
        </attr>
        <attr name="ntlUnderlineColor" format="color"/>
        <attr name="ntlUnderlineHeight" format="dimension"/>
        <attr name="ntlUnderlinePaddingTop" format="dimension"/>
        <attr name="ntlUnderlinePaddingBottom" format="dimension"/>

        <!-- Divider attrs -->
        <attr name="ntlShowDivider" format="boolean"/>
        <attr name="ntlDividerColor" format="color"/>
        <attr name="ntlDividerColors" format="reference"/>
        <attr name="ntlDividerWidth" format="dimension"/>
        <attr name="ntlDividerPadding" format="dimension"/>
        <attr name="ntlDividerPaddingTop" format="dimension"/>
        <attr name="ntlDividerPaddingBottom" format="dimension"/>
        <attr name="ntlDividerPaddingLeft" format="dimension"/>
        <attr name="ntlDividerPaddingRight" format="dimension"/>

        <!-- Indicator attrs -->
        <attr name="ntlShowIndicator" format="boolean"/>
        <attr name="ntlIndicatorGravity" format="enum">
            <enum name="top" value="0"/>
            <enum name="center" value="1"/>
            <enum name="bottom" value="2"/>
        </attr>
        <attr name="ntlIndicatorColor" format="color"/>
        <attr name="ntlIndicatorColors" format="reference"/>
        <attr name="ntlIndicatorHeight" format="dimension"/>
        <attr name="ntlIndicatorCornerRadius" format="dimension"/>
        <attr name="ntlIndicatorPaddingTop" format="dimension"/>
        <attr name="ntlIndicatorPaddingBottom" format="dimension"/>

        <!-- Tab attrs(for title and icon) -->
        <attr name="ntlDefaultTabColor" format="color"/>
        <attr name="ntlDefaultTabColors" format="reference"/>
        <attr name="ntlSelectedTabColor" format="color"/>
        <attr name="ntlSelectedTabColors" format="reference"/>
        <attr name="ntlTabPadding" format="dimension"/>
        <attr name="ntlTabPaddingTop" format="dimension"/>
        <attr name="ntlTabPaddingBottom" format="dimension"/>
        <attr name="ntlTabPaddingLeft" format="dimension"/>
        <attr name="ntlTabPaddingRight" format="dimension"/>
        <attr name="ntlTabViewLayoutId" format="reference"/>
        <attr name="ntlTabViewTextOrImageViewId" format="reference"/>
        <!-- Used when tab shows title and icon -->
        <attr name="ntlDrawablePadding" format="dimension"/>

        <!-- Title text attrs -->
        <attr name="ntlTextSize" format="dimension"/>
        <attr name="ntlTextAllCaps" format="boolean"/>
        <attr name="ntlTextStyle" format="enum">
            <enum name="normal" value="0"/>
            <enum name="bold" value="1"/>
            <enum name="italic" value="2"/>
            <enum name="boldItalic" value="3"/>
        </attr>

        <!-- Icon cross fade effect(only if you set icon drawable with selector(StateListDrawable), this will effect) -->
        <attr name="ntlIconCrossFade" format="boolean"/>
        <!-- If you set icon drawable with selector, set false gives better performance -->
        <attr name="ntlIconTint" format="boolean"/>

        <!-- Blur -->
        <attr name="ntlBlurRadius" format="integer" />
        <attr name="ntlDownSampleFactor" format="integer" />
        <attr name="ntlOverlayColor" format="color" />

        <!-- Badge -->
        <attr name="ntlBadgeGravity" format="enum">
            <enum name="left" value="0"/>
            <enum name="center_left" value="1"/>
            <enum name="center_right" value="2"/>
            <enum name="right" value="3"/>
        </attr>
        <attr name="ntlBadgeTextColor" format="color"/>
        <attr name="ntlBadgeTextSize" format="dimension"/>
        <attr name="ntlBadgeHeight" format="dimension" />
        <attr name="ntlBadgeCornerRadius" format="dimension" />
        <attr name="ntlBadgeMinWidth" format="dimension" />
        <attr name="ntlBadgeMaxWidth" format="dimension" />
        <attr name="ntlBadgeMarginLeft" format="dimension" />
        <attr name="ntlBadgeMarginRight" format="dimension" />
        <attr name="ntlBadgeMarginTop" format="dimension" />
        <attr name="ntlBadgePaddingLeftRight" format="dimension" />
        <attr name="ntlBadgeSmallSize" format="dimension" />
        <attr name="ntlBadgeBackground" format="reference|color"/>


*Almost all attributes have their respective setters to change them at runtime* , open an issue if you miss any.

# Developed By

 * Amiee Robot - <robot.amiee@gmail.com>
 * Check contributors list.

# Credits

 * [ogaclejapan SmartTabLayout](https://github.com/ogaclejapan/SmartTabLayout) - A custom ViewPager title strip which gives continuous feedback to the user when scrolling
 * [googlesamples SlidingTabsColors](https://github.com/googlesamples/android-SlidingTabsColors) - Android SlidingTabsColors Sample
 * [500px 500px-android-blur](https://github.com/500px/500px-android-blur) - Android Blurring View

# License

    Copyright 2015 Amiee Robot

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
