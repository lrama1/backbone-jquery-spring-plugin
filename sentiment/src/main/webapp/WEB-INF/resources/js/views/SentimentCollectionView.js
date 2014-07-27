define([ 'jquery', 'underscore', 'backbone', 'backgrid',
		'collections/SentimentCollection', 'globals/global',
		'text!templates/SentimentListTemplate.htm' /* the request for this template actually goes thru the MainController if its JSP*/
], function($, _, Backbone, Backgrid, SentimentCollection, Global,
		collectionTemplate) {

	var SentimentCollectionView = Backbone.View.extend({
		//standard backbone function called when a view is constructed
		initialize : function() {
			this.render();
		},
		render : function() {
			console.log("Models" + JSON.stringify(this.collection.models));
			var columns = [ {
				name : "sentimentSelector",
				cell : "select-row",
				headerCell : "select-all"
			}, {
				name : "id",
				cell : "string"
			}, {
				name : "sentimentText",
				cell : "string"
			}, {
				name : "sentimentResult",
				cell : "string"
			} ];

			// Initialize a new Grid instance
			var grid = new Backgrid.Grid({
				columns : columns,
				collection : this.collection
			//,footer: Backgrid.Extension.Paginator
			});
			//lets expose the grid
			this.grid = grid;

			// Initialize the paginator
			var paginator = new Backgrid.Extension.Paginator({
				collection : this.collection
			});

			// Render the grid and attach the root to your HTML document

			this.$el.html(_.template(collectionTemplate, {
				id : '',
				sentimentText : '',
				sentimentResult : ''
			}));
			$("#listContainer").html(grid.render().$el);
			$("#listContainer").append(paginator.render().$el);

			//this.collection.fetch({reset : true});
		},
		events : {
			"click #editSentimentButton" : "editSentiment",
			"click #filterButton" : "filter"
		},
		editSentiment : function() {
			if (this.grid.getSelectedModels().length > 0) {
				//location.hash = "sentiment/" + this.grid.getSelectedModels()[0].get("id");
				var idToFetch = this.grid.getSelectedModels()[0].get("id");
				require([ 'views/SentimentEditView', 'models/SentimentModel' ],
						function(SentimentEditView, SentimentModel) {
							var sentiment = new SentimentModel({
								id : idToFetch
							});
							var result = sentiment.fetch({
								success : function() {
									//render the view when Sentiment is fetched successfully	
									//Use this if you want to Edit in a Modal Dialog
									Global.showView(new SentimentEditView({
										el : $("#modalEditBody"),
										model : sentiment
									}));
									$('#myModal').modal('show');
								},
								error : function() {
									alert("problem");
								},
								silent : true
							});
						});

			} else {
				alert("Please select a Sentiment to edit.");
			}
		},
		filter : function() {
			this.collection.queryParams.filter = 'id' + ':'
					+ this.$("#id").val() + ';' + 'sentimentText' + ':'
					+ this.$("#sentimentText").val() + ';' + 'sentimentResult'
					+ ':' + this.$("#sentimentResult").val();
			alert('filtering the list');
			this.collection.fetch();
		}
	});

	return SentimentCollectionView;
});