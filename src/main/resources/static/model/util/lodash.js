'use strict';

/**
 * Lodash Extension.
 */
_.mixin({
	/**
	 * 驗證是否為數字，包含有小數及字串數字
	 * @param value
	 * @returns {boolean}
	 */
	isNumeric: function (value) {
		if (_.isString(value) && _.isBlank(value)) {
			return false;
		} else if (_.isNil(value)) {
			return false;
		}

		return !_.isNaN(_.toNumber(value));
	},
	/**
	 * 驗證是否為空值
	 * @param value
	 * @returns {boolean|*}
	 */
	isBlank: function (value) {
		return _.isNil(value) || (_.isString(value) && _.trim(value).length === 0);
	},
	/**
	 * 轉換中文數值
	 *
	 * @param value
	 * @param precision
	 * @returns {string|number}
	 */
	formatTwUnit: function (value, precision) {
		if (!_.isNumeric(value)) {
			return NaN;
		}

		var newValue = ['', '', ''];
		var fr = 1000;
		var num = 3;
		while (value / fr >= 1) {
			fr *= 10;
			num += 1;
		}
		if (num <= 4) {
			newValue[1] = '千';
			newValue[0] = _.round(parseInt(value / 1000), precision) + '';
		} else if (num <= 8) {
			// 萬
			var text1 = parseInt(num - 4) / 3 > 1 ? '千萬' : '萬';
			var fm = '萬' === text1 ? 10000 : 10000000;
			newValue[1] = text1;
			newValue[0] = _.round(value / fm, precision) + '';
		} else if (num <= 16) {
			var text1 = (num - 8) / 3 > 1 ? '千億' : '億';
			text1 = (num - 8) / 4 > 1 ? '萬億' : text1;
			text1 = (num - 8) / 7 > 1 ? '千萬億' : text1;
			var fm = 1;
			if ('億' === text1) {
				fm = 100000000;
			} else if ('千億' === text1) {
				fm = 100000000000;
			} else if ('萬億' === text1) {
				fm = 1000000000000;
			} else if ('千萬億' === text1) {
				fm = 1000000000000000;
			}
			newValue[1] = text1;
			newValue[0] = _.round(parseInt(value / fm), precision) + '';
		}
		if (value < 1000) {
			newValue[1] = '';
			newValue[0] = _.round(value, precision) + '';
		}
		return newValue.join('');
	},
	/**
	 * 驗證是否為正數
	 *
	 * @param value
	 * @returns {boolean|number}
	 */
	isPositive: function (value) {
		var regex = /^\d+(\.\d+)?$/;
		return regex.test(value);
	},
	/**
	 * 轉換input type 為Date的輸入格式
	 *
	 * @param value
	 * @returns {boolean|number}
	 */
	formatDate: function (value) {
		if (!value) {
			return null;
		}
		if (value.length === 10) {
			if (value.indexOf('/') !== 0) {
				return moment(value, 'YYYY/MM/DD').format('YYYY/MM/DD');
			} else if (value.indexOf('-') !== 0) {
				return moment(value, 'YYYY-MM-DD').format('YYYY-MM-DD');
			}
		}
		var mom = moment(value);
		if (mom.isValid()) {
			return mom.format('YYYY/MM/DD');
		}
		return 'Date Error';
	},
	/**
	 * 移除物件中，為null的屬性
	 *
	 * @param value
	 * @returns {object|number}
	 */
	filterNullProperties: function (obj) {
		// 遍歷物件的每個屬性
		for (var prop in obj) {
			if (obj.hasOwnProperty(prop)) {
				if (obj[prop] === null) {
					// 如果值為null
					delete obj[prop]; // 刪除該屬性
				} else if (typeof obj[prop] === 'object') {
					// 如果值為物件
					filterNullProperties(obj[prop]); // 遞歸處理該物件
				}
			}
		}
		return obj; // 返回過濾後的物件
	}
});
