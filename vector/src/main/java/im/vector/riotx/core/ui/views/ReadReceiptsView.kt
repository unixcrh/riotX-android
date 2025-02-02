/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.riotx.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import im.vector.riotx.R
import im.vector.riotx.features.home.AvatarRenderer
import im.vector.riotx.features.home.room.detail.timeline.item.ReadReceiptData
import kotlinx.android.synthetic.main.view_read_receipts.view.*

private const val MAX_RECEIPT_DISPLAYED = 5
private const val MAX_RECEIPT_DESCRIBED = 3

class ReadReceiptsView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val receiptAvatars: List<ImageView> by lazy {
        listOf(receiptAvatar1, receiptAvatar2, receiptAvatar3, receiptAvatar4, receiptAvatar5)
    }

    init {
        setupView()
    }

    private fun setupView() {
        inflate(context, R.layout.view_read_receipts, this)
    }

    fun render(readReceipts: List<ReadReceiptData>, avatarRenderer: AvatarRenderer, clickListener: OnClickListener) {
        setOnClickListener(clickListener)
        if (readReceipts.isNotEmpty()) {
            isVisible = true
            for (index in 0 until MAX_RECEIPT_DISPLAYED) {
                val receiptData = readReceipts.getOrNull(index)
                if (receiptData == null) {
                    receiptAvatars[index].visibility = View.INVISIBLE
                } else {
                    receiptAvatars[index].visibility = View.VISIBLE
                    avatarRenderer.render(receiptData.avatarUrl, receiptData.userId, receiptData.displayName, receiptAvatars[index])
                }
            }

            val displayNames = readReceipts
                    .mapNotNull { it.displayName }
                    .filter { it.isNotBlank() }
                    .take(MAX_RECEIPT_DESCRIBED)

            if (readReceipts.size > MAX_RECEIPT_DISPLAYED) {
                receiptMore.visibility = View.VISIBLE
                receiptMore.text = context.getString(
                        R.string.x_plus, readReceipts.size - MAX_RECEIPT_DISPLAYED
                )
            } else {
                receiptMore.visibility = View.GONE
            }
            contentDescription = when (readReceipts.size) {
                1    ->
                    if (displayNames.size == 1) {
                        context.getString(R.string.one_user_read, displayNames[0])
                    } else {
                        context.resources.getQuantityString(R.plurals.fallback_users_read, readReceipts.size)
                    }
                2    ->
                    if (displayNames.size == 2) {
                        context.getString(R.string.two_users_read, displayNames[0], displayNames[1])
                    } else {
                        context.resources.getQuantityString(R.plurals.fallback_users_read, readReceipts.size)
                    }
                3    ->
                    if (displayNames.size == 3) {
                        context.getString(R.string.three_users_read, displayNames[0], displayNames[1], displayNames[2])
                    } else {
                        context.resources.getQuantityString(R.plurals.fallback_users_read, readReceipts.size)
                    }
                else ->
                    if (displayNames.size >= 2) {
                        context.getString(R.string.two_and_some_others_read, displayNames[0], displayNames[1], (readReceipts.size - 2))
                    } else {
                        context.resources.getQuantityString(R.plurals.fallback_users_read, readReceipts.size)
                    }
            }
        } else {
            isVisible = false
        }
    }
}
