
import { useContext, createContext } from "react";

import {Instance, types} from "mobx-state-tree";
import {UserStore} from "./user";
import {CategoriesStore } from "./categories";
import { QuestionStore } from "./questions";

const RootStore = types.model("RootStore", {
    userStore: UserStore,
    categoriesStore: CategoriesStore,
    isLoading: types.boolean,
    questionsStore: QuestionStore,
    error: types.optional(types.string, ""),
});
export {RootStore};

type RootStoreModel = Instance<typeof RootStore>;

export const createStore = (): RootStoreModel => {
  const userStore = {id:1};
  const questionsStore = {};
  const answers = {id:0};
  const categoriesStore = {};
  const isLoading= true;

  return RootStore.create({ userStore, categoriesStore, isLoading, questionsStore});
};

// context to pass in compenents
const StoreContext = createContext<RootStoreModel>({} as RootStoreModel);

export const useStore = () => useContext(StoreContext);
export const StoreProvider = StoreContext.Provider;