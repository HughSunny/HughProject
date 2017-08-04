/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package set.work.view.recyclerview_swipe.touch;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Yan SXX   on 2016/8/1.
 * android.support.v7.widget.helper
 * Callback 获取不出来，所以要继承一下
 */
public class CompatItemTouchHelper extends ItemTouchHelper {
    protected Callback callback;
    public CompatItemTouchHelper(ItemTouchHelper.Callback callback) {
        super(callback);
        this.callback = callback;
    }

    /**
     * Developer callback which controls the behavior of ItemTouchHelper.
     *
     * @return {@link Callback}
     */
    public ItemTouchHelper.Callback getCallback() {
        return callback;
    }
}
