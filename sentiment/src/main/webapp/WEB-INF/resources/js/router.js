// Filename: router.js
define(
		[ 'jquery', 'underscore', 'backbone', 'backgrid', 'backgrid-paginator',
				'backgrid-select-all', 'bootstrap.min', 'globals/global'

		],
		function($, _, Backbone, Backgrid, BackgridPaginator,
				BackgridSelectAll, Bootstrap, Global) {

			//change underscore delims to using {{}}
			_.templateSettings = {
				evaluate : /{{([\s\S]+?)}}/g,
				interpolate : /{{=([\s\S]+?)}}/g
			};

			var AppRouter = Backbone.Router.extend({
				routes : {
					"sentiment/:id" : "getSentiment",
					"sentiments" : "getSentimentList"
				}
			});

			$(document).ready(function() {
				$('.nav li a').click(function(e) {
					$('.nav li').removeClass('active');
					var $parent = $(this).parent();
					if (!$parent.hasClass('active')) {
						$parent.addClass('active');
					}
					//e.preventDefault();
				});
			});

			var initialize = function() {

				// Initiate the router
				var app_router = new AppRouter;

				//FROM a Fragment
				//handler for getting a Sentiment record
				//	    app_router.on('route:getSentiment', function (idToFetch) {
				//	        // Note the variable in the route definition being passed in here
				//	    	require(['views/SentimentEditView', 'models/SentimentModel'], 
				//	    		function(SentimentEditView, SentimentModel){
				//			        var sentiment  = new SentimentModel({id : idToFetch});	
				//					var result = sentiment.fetch({
				//						success : function(){
				//							//render the view when Sentiment is fetched successfully	
				//							//Use this if you want to Edit in the same page
				//							app_router.showView(new SentimentEditView({ el: $("#bodyContainer"), model : sentiment }));
				//						},
				//						error : function(){
				//							alert("problem");
				//						},
				//						silent : true
				//					});
				//	    		});
				//	    });

				app_router
						.on(
								'route:getSentimentList',
								function() {
									console
											.log("getting the list of Sentiments.");
									require(
											[
													'collections/SentimentCollection',
													'views/SentimentCollectionView' ],
											function(SentimentCollection,
													SentimentCollectionView) {
												var collectionToFetch = new SentimentCollection();
												collectionToFetch
														.fetch({
															success : function(
																	collectionData) {
																Global
																		.showView(new SentimentCollectionView(
																				{
																					el : $("#bodyContainer"),
																					collection : collectionData
																				}));
															},
															error : function() {
																alert("Failed to obtain data for SentimentCollection()");
															}
														});

											});
								});

				// Start Backbone history a necessary step for bookmarkable URL's
				Backbone.history.start();

			};

			return {
				initialize : initialize
			};
		});