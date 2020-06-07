import { flow, getParent, types, Instance, getSnapshot } from "mobx-state-tree";

const Answer = types.model("Answer", {
    id: types.maybe(types.identifierNumber),
    text: types.optional(types.string, ""),
    user: types.maybeNull(types.integer),
    rating: types.optional(types.integer, 0),
    question: types.optional(types.integer, 0),
    voters: types.optional(types.array(types.integer), [])
  });