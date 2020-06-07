import {flow, types} from "mobx-state-tree"

const User = types.model("MinimalUser", {
    id: types.identifierNumber,
    username: types.optional(types.string, ""),
    gravatar_url: "",
});

const UserStore = types.model("UserStore", {
  id: types.maybe(types.identifierNumber),
  email: types.optional(types.string, ""),
  username: types.optional(types.string, ""),
  is_logined: types.optional(types.boolean, false),
  is_registered: types.optional(types.boolean, false),
  JWTtoken: types.optional(types.string, ""),

  loading: types.optional(types.boolean, false),
  loading_error: types.optional(types.string, ""),
  loading_errors: types.frozen(),
})

export { User, UserStore};