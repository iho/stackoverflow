import { CategoryComponent } from "../components/Category";
import { observer } from "mobx-react-lite";
import { useStore } from "../stores/rootStore";
import React, { useEffect } from "react";
import { Pagination } from "semantic-ui-react";
import { Segment } from "semantic-ui-react";
import { useParams, useHistory } from "react-router-dom";

const CategoriesListComponent = observer(() => {
  const { categoriesStore } = useStore();
  useEffect(() => {
    categoriesStore.getPage();
  }, []);
  let { id } = useParams();
  const history = useHistory();
  let total_pages = Math.floor(categoriesStore.total / 10);
  if (categoriesStore.total % 10 !== 0) {
    total_pages = total_pages + 1;
  }
  if (id === undefined) {
    id = 1;
  }
  return (
    <Segment>
      <Segment className="category-list">
        {categoriesStore.cats_by_page?.get(id)?.map((item) => (
          <CategoryComponent key={item.id} category={item} />
        ))}
      </Segment>
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
            history.push("/categories/" + data.activePage);
          }}
        />
      ) : (
        ""
      )}
    </Segment>
  );
});

export default CategoriesListComponent;
