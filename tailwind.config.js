module.exports = {
	content:
		process.env.NODE_ENV == "production"
			? ["./public/js/app.js"]
			: ["./src/yahtzure/**/*.cljs", "./public/js/cljs-runtime/*.js"],
};
