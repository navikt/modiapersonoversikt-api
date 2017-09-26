import { createStore, applyMiddleware } from 'redux';
import thunkMiddleware from 'redux-thunk';
import reducers from './reducers';

export const store = applyMiddleware(thunkMiddleware)(createStore)(reducers);
