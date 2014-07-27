define([ 'jquery', 'underscore', 'backbone', 'backgrid',
		'models/SentimentModel' ], function($, _, Backbone, backgrid,
		SentimentModel) {
	var SentimentCollection = Backbone.PageableCollection.extend({
		model : SentimentModel,
		url : '/sentiment/sentiments',
		state : {
			pageSize : 10
		},
		parse : function(response) {
			this.state.totalRecords = response.totalRecords;
			//this.state.totalPages = "2";
			this.state.lastPage = response.lastPage;
			console.log("Last Page: " + this.state.lastPage);
			//return response.sentiments;
			return response.rows;
		},

		mode : "server"
	});
	return SentimentCollection;
});