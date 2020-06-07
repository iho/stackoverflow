import { flow, getParent, types, Instance, getSnapshot } from "mobx-state-tree";
import { Question } from "./questions";

const Category = types.model("Category", {
  id: types.maybe(types.identifierNumber),
  name: types.optional(types.string, ""),
  slug: types.optional(types.string, ""),
  description: types.optional(types.string, ""),
});

const CategoriesStore = types
  .model("CategoriesStore", {
    cats_by_page: types.map(types.array(Category)),
    cat_by_id: types.map(Category),
    total: types.optional(types.integer, 0),
    loading: types.optional(types.boolean, false),
    loading_error: types.optional(types.string, ""),
    loading_errors: types.frozen(),
    current_page_number: types.optional(types.integer, 1),
  })
  .actions((self) => ({
    getPage: flow(function* getPage(page: number = 1) {
      // <- note the star, this a generator function!
      console.log("getAll()");
      const current_page = self.cats_by_page.get(String(page));
      if (current_page !== undefined) {
        return current_page;
      }
      try {
        self.loading = true;
        const response = yield fetch("/api/categories/?page=" + String(page), {
          method: "get",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
          },
        });
        const data = yield response.json();
        // data.results.forEach(
        //   (element: any) => (element.created_at = new Date(element.created_at))
        // );
        self.cats_by_page.set(String(page), data.results);
        self.total = data.count;
        // self.current_page = data.results;
        self.loading = false;
        console.log(data);
      } catch (error) {
        console.error("Failed to fetch projects", error);
      }
    }),
    getCategory: flow(function* getCategory(categoryId: number = 1) {
      const current_category = self.cat_by_id.get(String(categoryId));
      if (current_category !== undefined) {
        return;
      }
      try {
        self.loading = true;
        const categoryResponse = yield fetch(
          "/api/categories/" + String(categoryId),
          {
            method: "get",
            headers: {
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );
        const categoryData = yield categoryResponse.json();
        self.cat_by_id.set(String(categoryId), categoryData);
        console.log(categoryData);
        self.loading = false;
      } catch (error) {
        console.error("Failed to fetch projects", error);
      }
    }),
  }));

export { CategoriesStore, Category };
