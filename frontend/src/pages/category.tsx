import { observer } from "mobx-react-lite";
import { useStore } from "../stores/rootStore";
import React, { useEffect } from "react";
import { Pagination } from "semantic-ui-react";
import { Segment } from "semantic-ui-react";
import { useParams, useHistory, useRouteMatch } from "react-router-dom";


const CategoryDetailComponent = observer(() => {
  const { categoriesStore , questionsStore } = useStore();
  let { id, pageId } = useParams();
  const history = useHistory();
  let total_pages = Math.floor(categoriesStore.total / 10);
  if (categoriesStore.total % 10 !== 0) {
    total_pages = total_pages + 1;
  }
  if (pageId === undefined) {
    pageId = 1;
  }
  console.log(pageId);
  useEffect(() => {
    categoriesStore.getCategory(id);
    questionsStore.getPage(id, pageId);
  }, []);
  const category = categoriesStore.cat_by_id?.get(id);
  const questions = questionsStore.questions_by_page?.get("${categoryId}-${page}");
  total_pages = 1;
  console.log(questions);
  return (
    <Segment>
      <h1> {category?.name} </h1>
      {total_pages > 1 ? (
        <Pagination
          boundaryRange={0}
          defaultActivePage={categoriesStore.current_page_number}
          ellipsisItem={null}
          firstItem={null}
          lastItem={null}
          siblingRange={1}
          totalPages={total_pages}
          onPageChange={(event, data) => {
            categoriesStore.getPage(Number(data.activePage));
            history.push("/category/${category.id}/page/${data.activePage}");
          }}
        />
      ) : (
        ""
      )}
    </Segment>
  );
});

export default CategoryDetailComponent;
