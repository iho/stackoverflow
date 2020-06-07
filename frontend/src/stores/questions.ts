import { flow, getParent, types, Instance, getSnapshot } from "mobx-state-tree";

const Question = types.model("Question", {
    id: types.maybe(types.identifierNumber),
    name: types.optional(types.string, ""),
    slug: types.optional(types.string, ""),
    text: types.optional(types.string, ""),
    user: types.maybeNull(types.integer),
    rating: types.optional(types.integer, 0),
    category: types.optional(types.integer, 0),
    voters: types.optional(types.array(types.integer), [])
  });
const QuestionStore = types.model("CategoriesStore", {
  questions_by_page: types.map(types.array(Question)),
  total: types.optional(types.integer, 0),
  loading: types.optional(types.boolean, false),
}).actions(self =>({
  getPage: flow(function* getPage(categoryId:number, page:number=1) {
    self.loading = true;
    const response = yield fetch("/api/questions/?category="+ String(categoryId), {
      method: "get",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      }
    });
    const data = yield response.json();
    self.questions_by_page.set("${categoryId}-${page}", data.results);
    self.total = data.count;
    self.loading = false;

  })

}));

export {Question, QuestionStore};
  