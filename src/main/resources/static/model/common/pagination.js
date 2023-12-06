'use strict';

App.registry('component', 'vue-pagination', {
	template: '#vue-pagination-template',
	inject: ['config'],
	props: {
		gotoPage: Function,
		pageable: {
			default: null
		}, //分頁資料
		pageSize: {
			default: 5
		}, //最多顯示幾個分頁
		showInfo: {
			default: true
		} //是否顯示分頁資訊
	},
	data: function () {
		return {
			first: 0,
			last: 0,
			prev: 0,
			next: 0,
			pages: [],
			nowPage: 0,
			queryPage: 1,
			totalElements: 0,
			size: 0,
			numberOfElements: 0
		};
	},
	computed: {
		showPrev: function () {
			return this.prev >= 0 && this.nowPage !== this.first;
		},
		showNext: function () {
			return this.next >= 0 && this.nowPage !== this.last;
		}
	},
	watch: {
		pageable: {
			handler: function (newVal, oldVal) {
				var self = this;
				this.$nextTick(function () {
					self.changePageDebounce();
				});
				if (newVal.pageable) {
					self.queryPage = newVal.pageable.pageNumber + 1;
				}
			},
			immediate: true
		}
	},
	created: function () {},
	mounted: function () {
		var self = this;
		self.changePageDebounce = _.debounce(function () {
			self.changePage();
		}, 100);
	},
	methods: {
		clickPage: function (page) {
			if (page < 0) {
				return;
			}
			if (page < this.first || page > this.last) {
				return;
			}
			if (page === this.nowPage) {
				return;
			}
			this.queryPage = page + 1;
			this.gotoPage(page);
		},
		isNowPage: function (page) {
			return this.nowPage === page;
		},
		changePage: function () {
			if (_.isEmpty(this.pageable) || this.pageable.totalPages <= 0) {
				this.first = -1;
				this.last = -1;
				this.prev = -1;
				this.next = -1;
				this.pages = [];
				this.nowPage = -1;
				this.totalElements = -1;
				this.size = -1;
				this.numberOfElements = -1;
				return;
			}

			var halfSize = Math.floor(this.pageSize / 2);
			var start = this.pageable.number - halfSize;
			var rightAdd = 0;
			if (start < 0) {
				rightAdd = 0 - start;
				start = 0;
			}

			var end = this.pageable.number + halfSize + rightAdd;
			var endPage = this.pageable.totalPages - 1;
			var leftAdd = 0;
			if (end > endPage) {
				leftAdd = end - endPage;
				end = endPage;
			}

			start = start - leftAdd;
			if (start < 0) {
				start = 0;
			}

			var pages = [];
			for (var i = start; i <= end; i++) {
				pages.push(i);
			}

			var prevPage = this.pageable.number - 1;
			if (prevPage < 0) {
				prevPage = 0;
			}
			var nextPage = this.pageable.number + 1;
			if (nextPage > endPage) {
				nextPage = endPage;
			}

			this.pages = pages;
			this.first = 0;
			this.last = endPage;
			this.prev = prevPage;
			this.next = nextPage;
			this.nowPage = this.pageable.number;
			this.totalElements = this.pageable.totalElements;
			this.size = this.pageable.size;
			this.numberOfElements = this.pageable.numberOfElements;
		}
	}
});
