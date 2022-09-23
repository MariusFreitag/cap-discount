const cds = require('@sap/cds');

module.exports = class CatalogService extends cds.ApplicationService {
    init() {
        super.init();
        this.after('each', 'Books', (book) => {
            if (book.title && book.stock && book.stock > 200) {
                book.title +=  " (discounted)";
            }
        });
    }
};
