import { Card, Icon, Segment } from 'semantic-ui-react'
import React, { useEffect } from 'react'

import {Category} from '../stores/categories'
import { Instance } from 'mobx-state-tree';

import {Link} from "react-router-dom";
import { observer } from 'mobx-react-lite';

type CategoryType = Instance<typeof Category>

interface ICategoryProps {
  category: CategoryType
}

export const CategoryComponent:React.FC<ICategoryProps> = observer(props=> {
  const category = props.category;
  return (
    <Link to={"/category/"+ String(category.id)}>
    <Card>
      <Card.Content header={category.name} />
      <Card.Content description={category.description} />
      <Card.Content extra>
        <Icon name='question circle' />4 Questions
      </Card.Content>
    </Card>
    </Link>
)});