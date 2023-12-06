'use strict';

var App = {
	data: {
		el: '#app',
		registries: [],
		apiPath: null,
		contextPath: null,
		csrfToken: null,
		csrfHeaderName: null,
		notice: null,
		loader: null
	},
	config: function (config) {
		this.data = _.extend(this.data, config);
	},
	init: function (vueApp, opt) {
		var self = this;
		// Moment.js 多國語系
		self.initMoment();

		self.initAjax();

		self.initVue(vueApp, opt);
	},
	initAjax: function () {
		var self = this;
		var headers = {};
		headers[self.data.csrfHeaderName] = self.data.csrfToken;
		$.ajaxSetup({
			headers: headers,
			loading: true,
			loadingConfig: {
				container: null,
				loader: 'bars',
				color: '#3459e6'
			},
			beforeLoad: function (jqXhr, settings) {
				// vue-loading-layback move to ajaxStart()
			}
		});
		$(document).ajaxStart(function () {
			if (self.data.loader) {
				self.data.loader.hide();
			}
			self.data.loader = VueLoading.useLoading().show({
				container: null,
				loader: 'spinner',
				color: '#3459e6'
			});
		});
		$(document).ajaxStop(function () {
			if (self.data.loader) {
				self.data.loader.hide();
				self.data.loader = null;
			}
		});
		$.bi.ajaxStackError({
			throwMsg: function (error) {
				var self = this;
				var grid = '<table class="table">{0}</table>';
				var row = '<tr><td>{0}</td></tr>';
				var traceBt = '<a class="traceAlert" href="#" >(問題追蹤)</a><p style="display: none">{0}</p>';
				var msgs = _.map(error, function (item, i) {
					return row.format(item.msg + (item.trace ? traceBt.format(item.trace) : ''));
				}).join('');
				var msg = error[0].msg + (error[0].trace ? traceBt.format(error[0].trace) : '');

				Swal.fire({
					title: '訊息',
					html: error.length > 1 ? grid.format(msgs) : msg,
					showConfirmButton: false,
					showCloseButton: true,
					cancelButtonText: '<i class="fa fa-thumbs-down"></i>'
				});

				$(document)
					.off('click', '.traceAlert')
					.on('click', '.traceAlert', function () {
						var trace = $(this).parent().children('p').text();
						Swal.fire({
							width: 1200,
							html: `<div class="text-start text-sm">${trace}</div>`,
							showConfirmButton: false,
							showCloseButton: true,
							cancelButtonText: '<i class="fa fa-thumbs-down"></i>',
							didClose: function () {
								self.throwMsg(error);
							}
						});
					});
			},
			statusCallback: {
				401: function () {
					location.href = self.data.contextPath + '/login';
				},
				403: function () {
					location.href = self.data.contextPath + '/login';
				}
			}
		});
	},
	initMoment: function () {
		moment.updateLocale('zh-TW', {
			relativeTime: {
				future: '%s後',
				past: '%s前',
				s: '1 秒',
				ss: '%d 秒',
				m: '1 分鐘',
				mm: '%d 分鐘',
				h: '1 小時',
				hh: '%d 小時',
				d: '1 天',
				dd: '%d 天',
				w: '1 週',
				ww: '%d 週',
				M: '1 月',
				MM: '%d 月',
				y: '1 年',
				yy: '%d 年'
			}
		});
	},
	initVue: function (vueApp, opt) {
		var app = Vue.createApp(vueApp);
		app.provide('config', Vue.readonly(this.data));
		app.provide('prop', Vue.readonly(opt));
		this.initVueRegistry(app);
		app.mount('#app');
		return app;
	},
	initVueRegistry: function (app) {
		var registries = this.data.registries;
		var composites = [],
			stores = [],
			validations = [],
			filters = [];
		_.forEach(registries, function (item) {
			switch (item.type) {
				case 'store':
					stores.push(item);
					break;
				case 'valid':
					validations.push(item);
					break;
				case 'filter':
					filters.push(item);
					break;
				default:
					composites.push(item);
					break;
			}
		});
		this.initVueRegistryComposite(app, composites);
		this.initVueRegistryStore(app, stores);
		this.initVueRegistryValidation(validations);
		this.initVueRegistryFilter(app, filters);
	},
	initVueRegistryComposite: function (app, composites) {
		_.forEach(composites, function (item) {
			app[item.type](item.name, item.arg);
		});
	},
	initVueRegistryStore: function (app, stores) {
		var store = {};
		_.forEach(stores, function (item) {
			var name = item.name;
			if (!store[name]) {
				store[name] = item.arg;
			} else {
				throw new Error('Init vuex error: exist store name 「' + name + '」');
			}
		});
		app.use(
			new Vuex.Store({
				modules: store
			})
		);
	},
	initVueRegistryValidation: function (validations) {
		_.forEach(validations, function (item) {
			VeeValidate.defineRule(item.name, item.arg);
		});
		_.forEach(VeeValidateRules.default, function (val, key) {
			VeeValidate.defineRule(key, val);
		});
		VeeValidateI18n.loadLocaleFromURL(this.data.contextPath + '/model/validate/locale/zh_TW.json');
		// VeeValidateI18n.loadLocaleFromURL(this.data.contextPath + '/model/validate/locale/en.json');
		VeeValidate.configure({ generateMessage: VeeValidateI18n.localize('zh_TW') });
	},
	initVueRegistryFilter: function (app, filters) {
		var filter = {};
		_.forEach(filters, function (item) {
			var name = item.name;
			if (!filter[name]) {
				filter[name] = item.arg;
			} else {
				throw new Error('Init vuex error: exist filter name 「' + name + '」');
			}
		});
		app.config.globalProperties.$filters = filter;
	},
	registry: function (type, name, arg) {
		this.data.registries.push({
			type: type,
			name: name,
			arg: arg
		});
	}
};
