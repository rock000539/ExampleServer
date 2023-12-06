'use strict';

App.registry('component', 'vue-tree-menu', {
	template: '#vue-tree-menu-template',
	inject: ['config'],
	props: {
		config: Object,
		datas: {
			type: Array,
			required: true
		},
		parentChecked: Boolean,
		parentId: String,
		setParentDate: Function,
		//控制是否可以編輯
		isEdit: {
			type: Boolean,
			default: true
		}
	},
	data: function () {
		return {
			checkedDatas: [],
			customerBtnLabel: '選擇'
		};
	},
	watch: {
		checkedDatas: function (newVal) {
			var self = this;
			if (_.isEmpty(newVal)) {
				self.parentChecked = false;
			} else {
				self.parentChecked = true;
			}
			if (self.parentId) {
				self.setParentDate(self.parentId, self.parentChecked);
			}
		}
	},
	mounted: function () {
		var self = this;
		_.forEach(self.datas, function (item) {
			if (item.checked && !_.includes(self.checkedDatas, item.id)) {
				self.checkedDatas.push(item.id);
			}
		});
	},
	computed: {
		formattedDataList() {
			return this.datas.map((item) => {
				return { ...item, isShow: false, isHover: false };
			});
		}
	},
	methods: {
		initDatas: function () {
			var self = this;
			_.forEach(self.datas, function (item) {
				item.isShow = false;
			});
		},
		//展開收合全部
		toggleAll(expand) {
			this.datas.forEach((item) => {
				this.$set(item, 'isShow', expand);
				if (item.children && item.children.length > 0) {
					var childMenus = this.$refs.childMenus;
					childMenus.forEach((node) => {
						node.toggleAll(expand);
					});
				}
			});
		},
		//展開收合單一項目
		toggleExpand: function (item) {
			this.$set(item, 'isShow', !item.isShow);
		},
		//取得所有選取結果用
		getCheckedDatas: function () {
			var self = this;
			var childMenus = this.$refs.childMenus;
			if (!childMenus) {
				return self.checkedDatas;
			}
			childMenus.forEach((node) => {
				self.checkedDatas = self.checkedDatas.concat(node.getCheckedDatas());
			});
			return self.checkedDatas;
		},
		//子物件被選取，更改上層checkedDatas用
		toggleCheckedDate: function (data, checked) {
			var self = this;
			if (checked == true && !_.includes(self.checkedDatas, data)) {
				self.checkedDatas.push(data);
			} else if (checked == false) {
				self.checkedDatas.forEach(function (item, index, arr) {
					if (item == data) {
						arr.splice(index, 1);
					}
				});
			}
		},
		//物件被選取，更改下層checkedDatas用
		checked: function (data) {
			var self = this;
			var checked = false;

			if (!_.includes(self.checkedDatas, data)) {
				self.checkedDatas.push(data);
				checked = true;
			} else {
				self.checkedDatas.forEach(function (item, index, arr) {
					if (item == data) {
						arr.splice(index, 1);
					}
				});
			}

			var childMenus = this.$refs.childMenus;
			if (!childMenus) {
				return;
			}
			childMenus.forEach((childMenu) => {
				if (childMenu.parentId == data) {
					childMenu.toggleChildDatas(checked);
				}
			});
		},
		toggleChildDatas: function (checked) {
			var self = this;
			self.checkedDatas = [];
			if (checked) {
				_.forEach(self.datas, function (item) {
					self.checkedDatas.push(item.id);
				});
			}

			var childMenus = this.$refs.childMenus;
			if (childMenus) {
				childMenus.forEach((childMenu) => {
					childMenu.toggleChildDatas(checked);
				});
			}
		}
	}
});
