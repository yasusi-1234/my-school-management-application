package com.example.demo.controller.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PageWrapper<T> {

	@Getter
	private Page<T> page;
	@Getter
	@Setter
	private int maximumNumberItems;
	@Getter
	@Setter
	private String url;

	@Getter
	private List<PageItem> items;
	@Getter
	private int currentNum;

	public PageWrapper(Page<T> page, int muximumNumberItems, String url) {
		super();
		this.page = page;
		this.url = url;
		// 後に偶数は許容しない設計にする
		this.maximumNumberItems = Math.min(page.getTotalPages(), muximumNumberItems);

		this.currentNum = page.getNumber() + 1;

		// pageItemの作成位置を決定するための変数
		int first, last;
		// 前方の方のページか
		if (currentNum <= maximumNumberItems - maximumNumberItems / 2) {
			first = 1;
			last = maximumNumberItems;
		} else {
			// 後方の方のページか
			if (currentNum >= page.getTotalPages() - maximumNumberItems / 2) {
				int setnum = page.getTotalPages() - (maximumNumberItems / 2) * 2;
				first = setnum == 0 ? 1 : setnum;
				last = page.getTotalPages();
				// 中央の方のページか
			} else {
				first = currentNum - maximumNumberItems / 2;
				last = currentNum + maximumNumberItems / 2;
			}
//			else if(currentNum > maximumNumberItems - maximumNumberItems / 2 && 
//					currentNum < page.getTotalPages() - maximumNumberItems / 2){
//				first = currentNum - maximumNumberItems / 2;
//				last = currentNum + maximumNumberItems / 2;
//			}
		}
		// PageItemのリスト生成
		items = new ArrayList<>();
		for (int i = first; i <= last; i++) {
			items.add(new PageItem(i, i == currentNum));
		}
	}

	public int getSize() {
		return page.getSize();
	}

	public int getNumber() {
		return page.getNumber();
	}

	public boolean isFirst() {
		return page.isFirst();
	}

	public boolean isLast() {
		return page.isLast();
	}

	public boolean hasPrevious() {
		return page.hasPrevious();
	}

	public boolean hasNext() {
		return page.hasNext();
	}

	public long getTotalElements() {
		return page.getTotalElements();
	}

	public int getTotalPages() {
		return page.getTotalPages();
	}

	public List<T> getContent() {
		return page.getContent();
	}

	@Data
	@AllArgsConstructor
	public class PageItem {
		private int number;
		private boolean current;
	}
}

