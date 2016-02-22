import { createStore, applyMiddleware, compose } from 'redux';
import thunkMiddleware from 'redux-thunk';
import reducers from './reducers';

export const store = applyMiddleware(thunkMiddleware)(createStore)(reducers);
