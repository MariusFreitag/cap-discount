const cds = require('@sap/cds');

module.exports = class CatalogService extends cds.ApplicationService {
    init() {
        super.init();
        this.after('each', 'Books', (book) => {
            if (book.title && book.stock && book.stock >= 500) {
                book.title += " (discounted)";
            }
        });
        this.on('decreaseStock', 'Books', async (event) => {
            const book = await SELECT.from(this.entities.Books, event.params[0]);
            await UPDATE(this.entities.Books, event.params[0]).with({ stock: book.stock - 1 });
        });
    }
};
